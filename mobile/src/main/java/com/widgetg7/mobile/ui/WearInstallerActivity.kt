package com.widgetg7.mobile.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.widgetg7.mobile.R
import com.widgetg7.mobile.watch.install.WearDirectAdbInstaller
import com.widgetg7.mobile.watch.install.WearEmbeddedApkRepository
import com.widgetg7.mobile.watch.install.WearInstallOcr
import com.widgetg7.mobile.watch.install.WearInstallOcrParsed
import com.widgetg7.mobile.watch.install.WearInstallOcrParser
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Aide à installer l’app montre : installation directe (ADB Wi‑Fi via Kadb),
 * ou partage du fichier embarqué en secours.
 */
class WearInstallerActivity : AppCompatActivity() {
    private var baseScrollPaddingTop = 0

    private lateinit var installerScroll: ScrollView
    private lateinit var directSection: View
    private lateinit var embeddedRepo: WearEmbeddedApkRepository
    private lateinit var shareButton: MaterialButton
    private lateinit var pairButton: MaterialButton
    private lateinit var installDirectButton: MaterialButton
    private lateinit var ocrButton: MaterialButton
    private lateinit var directProgress: ProgressBar
    private lateinit var directStatus: TextView
    private lateinit var ipInput: TextInputEditText
    private lateinit var pairPortInput: TextInputEditText
    private lateinit var pairCodeInput: TextInputEditText
    private lateinit var adbPortInput: TextInputEditText

    private lateinit var cameraCaptureUri: Uri

    private val pickImageForOcr =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { runOcrOnUri(it) }
        }

    private val takePictureForOcr =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                runOcrOnUri(cameraCaptureUri)
            } else {
                showDirectStatus(getString(R.string.wear_install_direct_ocr_photo_cancelled), error = true)
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                launchWearOcrCameraCapture()
            } else {
                showDirectStatus(getString(R.string.wear_install_direct_ocr_permission), error = true)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_installer)

        embeddedRepo = WearEmbeddedApkRepository(this)

        val scroll = findViewById<ScrollView>(R.id.wearInstallerScroll)
        installerScroll = scroll
        directSection = findViewById(R.id.wearInstallerDirectCard)
        baseScrollPaddingTop = scroll.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                baseScrollPaddingTop + bars.top,
                view.paddingRight,
                view.paddingBottom + bars.bottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(scroll)

        findViewById<ImageButton>(R.id.wearInstallerBack).setOnClickListener { finish() }

        val infoText = findViewById<TextView>(R.id.wearInstallerApkInfo)
        shareButton = findViewById(R.id.wearInstallerShareApk)

        ipInput = findViewById(R.id.wearInstallerDirectIpInput)
        pairPortInput = findViewById(R.id.wearInstallerDirectPairPortInput)
        pairCodeInput = findViewById(R.id.wearInstallerDirectPairCodeInput)
        adbPortInput = findViewById(R.id.wearInstallerDirectAdbPortInput)
        pairButton = findViewById(R.id.wearInstallerDirectPairButton)
        installDirectButton = findViewById(R.id.wearInstallerDirectInstallButton)
        ocrButton = findViewById(R.id.wearInstallerDirectOcrButton)
        directProgress = findViewById(R.id.wearInstallerDirectProgress)
        directStatus = findViewById(R.id.wearInstallerDirectStatus)

        pairButton.setOnClickListener { runPairWireless() }
        installDirectButton.setOnClickListener { runDirectInstall() }
        ocrButton.setOnClickListener { showWearOcrSourceDialog() }

        if (embeddedRepo.embeddedApkAvailable()) {
            val size = embeddedRepo.embeddedFileSizeBytes()?.let(WearEmbeddedApkRepository::formatSize) ?: "—"
            infoText.text = getString(R.string.wear_install_apk_info_simple, size)
            shareButton.visibility = View.VISIBLE
            shareButton.setOnClickListener { shareEmbeddedApk() }
            installDirectButton.isEnabled = true
            installDirectButton.alpha = 1f
        } else {
            infoText.text = getString(R.string.wear_install_apk_missing)
            shareButton.visibility = View.GONE
            installDirectButton.isEnabled = false
            installDirectButton.alpha = 0.5f
        }
    }

    private fun runPairWireless() {
        val host = ipInput.text?.toString()?.trim().orEmpty()
        val pairPort = pairPortInput.text?.toString()?.trim()?.toIntOrNull()
        val code = pairCodeInput.text?.toString()?.trim().orEmpty()
        if (host.isEmpty() || pairPort == null || code.length != 6) {
            showDirectStatus(getString(R.string.wear_install_direct_err_pair_fields), error = true)
            return
        }
        setDirectBusy(true)
        lifecycleScope.launch {
            val result =
                withContext(Dispatchers.IO) {
                    runCatching { WearDirectAdbInstaller.pair(host, pairPort, code) }
                }
            setDirectBusy(false)
            result.fold(
                onSuccess = { showDirectStatus(getString(R.string.wear_install_direct_pair_ok), error = false) },
                onFailure = { t ->
                    showDirectStatus(
                        getString(R.string.wear_install_direct_err_generic, t.message ?: t.javaClass.simpleName),
                        error = true,
                    )
                },
            )
        }
    }

    private fun runDirectInstall() {
        val host = ipInput.text?.toString()?.trim().orEmpty()
        val adbPort = adbPortInput.text?.toString()?.trim()?.toIntOrNull()
        if (host.isEmpty() || adbPort == null) {
            showDirectStatus(getString(R.string.wear_install_direct_err_fill), error = true)
            return
        }
        val apk = embeddedRepo.copyToCacheForShare()
        if (apk == null || !apk.exists()) {
            showDirectStatus(getString(R.string.wear_install_apk_missing), error = true)
            return
        }
        setDirectBusy(true)
        lifecycleScope.launch {
            val result =
                withContext(Dispatchers.IO) {
                    runCatching { WearDirectAdbInstaller.installApk(host, adbPort, apk) }
                }
            setDirectBusy(false)
            result.fold(
                onSuccess = { showDirectStatus(getString(R.string.wear_install_direct_install_ok), error = false) },
                onFailure = { t ->
                    showDirectStatus(
                        getString(R.string.wear_install_direct_err_generic, t.message ?: t.javaClass.simpleName),
                        error = true,
                    )
                },
            )
        }
    }

    private fun setDirectBusy(busy: Boolean) {
        pairButton.isEnabled = !busy
        ocrButton.isEnabled = !busy
        installDirectButton.isEnabled = !busy && embeddedRepo.embeddedApkAvailable()
        shareButton.isEnabled = !busy && embeddedRepo.embeddedApkAvailable()
        directProgress.visibility = if (busy) View.VISIBLE else View.GONE
    }

    private fun showWearOcrSourceDialog() {
        val options =
            arrayOf(
                getString(R.string.wear_install_direct_ocr_camera),
                getString(R.string.wear_install_direct_ocr_gallery),
            )
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.wear_install_direct_ocr_title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCameraForWearOcr()
                    1 ->
                        pickImageForOcr.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                }
            }
            .setNegativeButton(R.string.wear_install_direct_ocr_cancel, null)
            .show()
    }

    private fun openCameraForWearOcr() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> launchWearOcrCameraCapture()
            else -> requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchWearOcrCameraCapture() {
        val file = File(cacheDir, "wear_install_ocr_${System.currentTimeMillis()}.jpg")
        try {
            file.parentFile?.mkdirs()
            if (!file.exists() && !file.createNewFile()) {
                showDirectStatus(getString(R.string.wear_install_direct_ocr_decode_error), error = true)
                return
            }
        } catch (_: Exception) {
            showDirectStatus(getString(R.string.wear_install_direct_ocr_decode_error), error = true)
            return
        }
        cameraCaptureUri =
            FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        takePictureForOcr.launch(cameraCaptureUri)
    }

    private fun runOcrOnUri(uri: Uri) {
        showDirectStatus(getString(R.string.wear_install_direct_ocr_running), error = false)
        setDirectBusy(true)
        lifecycleScope.launch {
            val outcome =
                withContext(Dispatchers.IO) {
                    runCatching {
                        val raw = WearInstallOcr.recognizeText(this@WearInstallerActivity, uri)
                        WearInstallOcrParser.parse(raw)
                    }
                }
            setDirectBusy(false)
            outcome.fold(
                onSuccess = { parsed -> applyOcrParsed(parsed) },
                onFailure = { t ->
                    val message =
                        if (t is IllegalStateException && t.message == WearInstallOcr.DECODE_FAILED_MESSAGE) {
                            getString(R.string.wear_install_direct_ocr_decode_error)
                        } else {
                            getString(
                                R.string.wear_install_direct_ocr_error,
                                t.message ?: t.javaClass.simpleName,
                            )
                        }
                    showDirectStatus(message, error = true)
                },
            )
        }
    }

    private fun clearDirectInstallInputs() {
        ipInput.text = null
        pairPortInput.text = null
        pairCodeInput.text = null
        adbPortInput.text = null
    }

    private fun scrollDirectSectionIntoView() {
        installerScroll.post {
            val target = maxOf(0, directSection.top - 24)
            installerScroll.smoothScrollTo(0, target)
        }
    }

    private fun applyOcrParsed(parsed: WearInstallOcrParsed) {
        ipInput.post {
            clearDirectInstallInputs()
            var filled = false
            parsed.ip?.let {
                ipInput.setText(it)
                ipInput.text?.length?.let { len -> ipInput.setSelection(len) }
                filled = true
            }
            parsed.pairPort?.let {
                val s = it.toString()
                pairPortInput.setText(s)
                pairPortInput.text?.length?.let { len -> pairPortInput.setSelection(len) }
                filled = true
            }
            parsed.pairCode?.let {
                pairCodeInput.setText(it)
                pairCodeInput.text?.length?.let { len -> pairCodeInput.setSelection(len) }
                filled = true
            }
            parsed.adbPort?.let {
                val s = it.toString()
                adbPortInput.setText(s)
                adbPortInput.text?.length?.let { len -> adbPortInput.setSelection(len) }
                filled = true
            }
            scrollDirectSectionIntoView()
            val complete =
                parsed.ip != null &&
                    parsed.pairPort != null &&
                    parsed.pairCode != null &&
                    parsed.adbPort != null
            when {
                !filled -> showDirectStatus(getString(R.string.wear_install_direct_ocr_none), error = true)
                parsed.ip == null ->
                    showDirectStatus(getString(R.string.wear_install_direct_ocr_no_ip), error = true)
                complete ->
                    showDirectStatus(getString(R.string.wear_install_direct_ocr_complete), error = false)
                else -> showDirectStatus(getString(R.string.wear_install_direct_ocr_partial), error = false)
            }
        }
    }


    private fun showDirectStatus(message: String, error: Boolean) {
        directStatus.visibility = View.VISIBLE
        directStatus.text = message
        directStatus.setTextColor(
            ContextCompat.getColor(
                this,
                if (error) R.color.wg7_danger else R.color.wg7_accent_dark,
            ),
        )
    }

    private fun shareEmbeddedApk() {
        val file = embeddedRepo.copyToCacheForShare()
        if (file == null || !file.exists()) {
            Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.wear_install_share_error),
                Snackbar.LENGTH_LONG,
            ).show()
            return
        }
        val uri =
            FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file,
            )
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.wear_install_share_subject))
        }
        startActivity(Intent.createChooser(send, getString(R.string.wear_install_share_chooser)))
    }
}
