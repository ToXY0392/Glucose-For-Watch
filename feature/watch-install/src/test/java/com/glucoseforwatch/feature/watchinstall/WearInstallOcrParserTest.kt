package com.glucoseforwatch.feature.watchinstall

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Fictitious LAN addresses and codes only. */
class WearInstallOcrParserTest {
    @Test
    fun empty_text_yields_no_fields() {
        assertEquals(WearInstallOcrParsed(), WearInstallOcrParser.parse(""))
        assertEquals(WearInstallOcrParsed(), WearInstallOcrParser.parse("   \n  "))
    }

    @Test
    fun preprocess_fixes_letter_o_read_between_digits() {
        assertEquals("192.168.0.23", WearInstallOcrParser.preprocessOcrRaw("192.168.O.23"))
        assertEquals("480915", WearInstallOcrParser.preprocessOcrRaw("48O915"))
        assertEquals("a b", WearInstallOcrParser.preprocessOcrRaw("a b"))
    }

    @Test
    fun wireless_debugging_screen_gives_ip_and_adb_port() {
        val parsed =
            WearInstallOcrParser.parse(
                "Wireless debugging\nIP address & port\n192.168.1.23:41235",
            )

        assertEquals("192.168.1.23", parsed.ip)
        assertEquals(41235, parsed.adbPort)
        assertNull(parsed.pairCode)
        assertNull(parsed.pairPort)
    }

    @Test
    fun letter_o_misread_in_ip_is_repaired() {
        val parsed =
            WearInstallOcrParser.parse(
                "Wireless debugging\nIP address & port\n192.168.O.23:41235",
            )

        assertEquals("192.168.0.23", parsed.ip)
        assertEquals(41235, parsed.adbPort)
    }

    @Test
    fun spaced_pairing_code_is_joined() {
        val parsed = WearInstallOcrParser.parse("Wi-Fi pairing code\n48 29 15")

        assertEquals("482915", parsed.pairCode)
        assertNull(parsed.ip)
        assertNull(parsed.pairPort)
        assertNull(parsed.adbPort)
    }

    @Test
    fun non_lan_addresses_are_ignored() {
        assertNull(WearInstallOcrParser.parse("224.0.0.1").ip)
        assertNull(WearInstallOcrParser.parse("0.10.20.30").ip)
    }
}
