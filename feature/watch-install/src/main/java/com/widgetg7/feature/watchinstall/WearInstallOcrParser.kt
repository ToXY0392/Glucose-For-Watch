package com.widgetg7.feature.watchinstall

/** Pairing fields extracted from a watch wireless-debug OCR capture. */
data class WearInstallOcrParsed(
    val ip: String? = null,
    val pairPort: Int? = null,
    val pairCode: String? = null,
    val adbPort: Int? = null,
)

/**
 * Parses watch wireless-debug screenshots for IP, pairing port/code, and ADB port.
 *
 * Tolerates common OCR misreads (O/0, spaced digits, multiline layouts).
 */
object WearInstallOcrParser {
    internal fun preprocessOcrRaw(raw: String): String {
        var s = raw.replace('\u00a0', ' ')
        s = s.replace(Regex("""(?<=[\d.])[Oo](?=[\d.])"""), "0")
        s = s.replace(Regex("""(?<=\d)[Oo](?=\d)"""), "0")
        return s
    }

    private val ipv4Strict =
        Regex("""\b(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\b""")
    private val ipv4Loose = Regex("""\b(?:\d{1,3}\s*\.\s*){3}\d{1,3}\b""")
    private val sixDigitCode = Regex("""(?<![0-9])(\d{6})(?![0-9])""")
    private val sixDigitCodeSpaced = Regex("""(?<![0-9])((?:\d\s*){6})(?![0-9])""")
    private val standaloneFiveDigitLine = Regex("""^\s*(\d{4,5})\s*$""")
    private val adbContextHints =
        Regex("""(?i)(ip\s+address\s*(\u0026|and|\+)?\s*port|address\s*&\s*port|(?:wireless|wifi).*debug|d(e|é)bogage[^\n]{0,12}wi)""")
    private val ipColonPort =
        Regex("""(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\s*[:#]\s*(\d{4,5})\b""", RegexOption.IGNORE_CASE)
    private val ipColonPortLoose =
        Regex("""(?:\d{1,3}\s*\.\s*){3}\d{1,3}\s*[:#]\s*(\d{4,5})\b""", RegexOption.IGNORE_CASE)
    private val portToken = Regex("""\b(\d{4,5})\b""")

    /** Extracts pairing fields from raw ML Kit OCR text. */
    fun parse(raw: String): WearInstallOcrParsed {
        val cleaned = preprocessOcrRaw(raw)
        val text = normalizeOcrText(cleaned)
        if (text.isEmpty()) return WearInstallOcrParsed()

        val lineNormals =
            cleaned.split(Regex("""\r?\n""")).map { normalizeOcrText(it) }.filter { it.isNotEmpty() }

        val ip = findIpv4(text)
        val pairCode = findSixDigitCode(text)
        val pairPortFromLayout = inferPairPortBetweenIpAndCode(lineNormals, ip, pairCode)
        val adbPortFromKw = inferAdbPortFromKeywordLines(lineNormals, ip)

        val colonPorts =
            buildList {
                addAll(ipColonPort.findAll(text).mapNotNull { it.groupValues.getOrNull(1)?.toIntOrNull()?.takeIf { n -> n in PORT_RANGE } })
                if (isEmpty()) {
                    addAll(ipColonPortLoose.findAll(text).mapNotNull { it.groupValues.getOrNull(1)?.toIntOrNull()?.takeIf { n -> n in PORT_RANGE } })
                }
            }

        val adbFromColon = colonPorts.lastOrNull()
        var adbPort: Int? = adbPortFromKw ?: adbFromColon
        val standalonePorts = mutableListOf<Int>()
        for (m in portToken.findAll(text)) {
            val n = m.groupValues[1].toIntOrNull() ?: continue
            if (n in PORT_RANGE) standalonePorts.add(n)
        }
        val orderedDistinctPorts = distinctInOrder(standalonePorts)
        var pairPort: Int? = pairPortFromLayout

        val portsForPair = orderedDistinctPorts.filter { adbPort == null || it != adbPort }.toMutableList()
        when {
            adbPort != null -> if (pairPort == null) pairPort = portsForPair.firstOrNull { it != adbPort }
            orderedDistinctPorts.size >= 2 -> {
                if (pairPort == null) pairPort = orderedDistinctPorts[0]
                adbPort = orderedDistinctPorts[1]
            }
            orderedDistinctPorts.size == 1 -> if (pairPort == null) pairPort = orderedDistinctPorts[0]
        }

        if (pairPort == null && colonPorts.size >= 2) {
            pairPort = colonPorts.first()
            if (adbPort == null) adbPort = colonPorts.last()
        }

        return WearInstallOcrParsed(ip = ip, pairPort = pairPort, pairCode = pairCode, adbPort = adbPort)
    }

    private fun inferPairPortBetweenIpAndCode(lines: List<String>, ip: String?, pairCode: String?): Int? {
        if (lines.isEmpty() || ip.isNullOrEmpty()) return null
        val ipLineIdx = lines.indexOfFirst { it.contains(ip) }.takeIf { it >= 0 } ?: return null
        val codeLineIdx = if (pairCode != null) {
            lines.indexOfFirst { Regex("""\b${Regex.escape(pairCode)}\b""").containsMatchIn(it) }.takeIf { it >= 0 }
        } else {
            lines.indexOfFirst { Regex("""^\s*\d{6}\s*$""").matches(it) }.takeIf { it >= 0 }
        }
        val searchUntil = codeLineIdx ?: lines.size
        for (j in ipLineIdx + 1 until searchUntil) {
            val m = standaloneFiveDigitLine.matchEntire(lines[j]) ?: continue
            val p = m.groupValues[1].toIntOrNull() ?: continue
            if (p in PORT_RANGE) return p
        }
        return null
    }

    private fun inferAdbPortFromKeywordLines(lines: List<String>, ip: String?): Int? {
        if (lines.isEmpty()) return null
        for (i in lines.indices) {
            val line = lines[i]
            if (!adbContextHints.containsMatchIn(line)) continue
            val window = sequenceOf(lines.getOrNull(i), lines.getOrNull(i + 1)).filterNotNull().joinToString(" ")
            extractPortFromIpColon(window)?.let { return it }
        }
        if (ip != null) {
            val ipIdx = lines.indexOfFirst { it.contains(ip) }
            if (ipIdx >= 0) {
                for (delta in 0..2) {
                    val j = ipIdx + delta
                    if (j + 1 >= lines.size) break
                    val concat = "${lines[j]} ${lines[j + 1]}".replace(Regex("""\s+\.\s+"""), ".")
                    extractPortFromIpColon(concat)?.let { return it }
                }
            }
        }
        return null
    }

    private fun extractPortFromIpColon(blob: String): Int? {
        val m = ipColonPort.find(blob) ?: ipColonPortLoose.find(blob) ?: return null
        return m.groupValues.getOrNull(1)?.toIntOrNull()?.takeIf { it in PORT_RANGE }
    }

    private fun normalizeOcrText(raw: String): String {
        var t = raw.replace('\r', ' ').replace('\n', ' ')
        var prev: String
        do {
            prev = t
            t = t.replace(Regex("""(\d)\s*\.\s*(\d)"""), "$1.$2")
        } while (t != prev)
        t = t.replace(Regex("""\b(\d{2})\s+(\d{3})\b"""), "$1$2")
        t = t.replace(Regex("""\b(\d{3})\s+(\d{2})\b"""), "$1$2")
        t = t.replace(Regex("""\b(\d{3})\s+(\d{3})\b"""), "$1$2")
        t = t.replace(Regex("""(\d)\s+(?=\d)"""), "$1")
        return t.replace(Regex("""\s+"""), " ").trim()
    }

    private fun findIpv4(text: String): String? {
        ipv4Strict.findAll(text).map { it.value }.firstOrNull(::isPlausibleLanIp)?.let { return it }
        for (m in ipv4Loose.findAll(text)) {
            val compact = m.value.replace(Regex("""\s+"""), "")
            if (isValidIpv4Quad(compact) && isPlausibleLanIp(compact)) return compact
        }
        return null
    }

    private fun findSixDigitCode(text: String): String? {
        sixDigitCode.find(text)?.groupValues?.get(1)?.let { return it }
        sixDigitCodeSpaced.find(text)?.groupValues?.get(1)?.let { chunk ->
            val digits = chunk.filter { it.isDigit() }
            if (digits.length == 6) return digits
        }
        for (m in Regex("""\b[\dOoIl|]{6}\b""").findAll(text)) {
            val inferred = m.value.map { ch ->
                when (ch) {
                    'O', 'o' -> '0'
                    'l', 'L', 'I', 'i', '|' -> '1'
                    else -> ch
                }
            }.joinToString("")
            if (inferred.all { it.isDigit() }) return inferred
        }
        return null
    }

    private val PORT_RANGE = 1024..65535

    private fun isPlausibleLanIp(ip: String): Boolean {
        val firstOctet = ip.substringBefore('.').toIntOrNull() ?: return false
        if (firstOctet == 0 || firstOctet >= 224) return false
        return true
    }

    private fun isValidIpv4Quad(s: String): Boolean {
        val parts = s.split('.')
        if (parts.size != 4) return false
        return parts.all { p -> p.toIntOrNull()?.let { it in 0..255 } == true }
    }

    private fun distinctInOrder(ports: List<Int>): List<Int> {
        val seen = HashSet<Int>()
        val out = ArrayList<Int>(ports.size)
        for (n in ports) {
            if (seen.add(n)) out.add(n)
        }
        return out
    }
}
