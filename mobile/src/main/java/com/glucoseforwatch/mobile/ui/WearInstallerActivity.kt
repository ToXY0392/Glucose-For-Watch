package com.glucoseforwatch.mobile.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.glucoseforwatch.feature.watchinstall.WearDirectAdbInstaller
import com.glucoseforwatch.feature.watchinstall.WearEmbeddedApkRepository
import com.glucoseforwatch.feature.watchinstall.WearInstallOcr
import com.glucoseforwatch.feature.watchinstall.WearInstallOcrParsed
import com.glucoseforwatch.feature.watchinstall.WearInstallOcrParser
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.ui.compose.WearInstallerScreen
import com.glucoseforwatch.mobile.ui.compose.WearInstallerUiState
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Walks through direct Wear app install via ADB Wi-Fi (Kadb). */
class WearInstallerActivity : ComponentActivity() {
    private lateinit var embeddedRepo: WearEmbeddedApkRepository
    private var uiState by mutableStateOf(WearInstallerUiState())
    private var scrollToDirectToken by mutableIntStateOf(0)

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
        embeddedRepo = WearEmbeddedApkRepository(this)
        val apkAvailable = embeddedRepo.embeddedApkAvailable()
        uiState =
            uiState.copy(
                installEnabled = apkAvailable,
                showEmbeddedApkMissing = !apkAvailable,
            )

        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                WearInstallerScreen(
                    state = uiState.copy(scrollToDirectSectionToken = scrollToDirectToken),
                    onIpChange = { uiState = uiState.copy(ip = it) },
                    onPairPortChange = { uiState = uiState.copy(pairPort = it) },
                    onPairCodeChange = { uiState = uiState.copy(pairCode = it) },
                    onAdbPortChange = { uiState = uiState.copy(adbPort = it) },
                    onOcrClick = ::showWearOcrSourceDialog,
                    onPairClick = ::runPairWireless,
                    onInstallClick = ::runDirectInstall,
                    onBack = { finish() },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            colorResource(R.color.gfw_canvas_start),
                                            colorResource(R.color.gfw_canvas_end),
                                        ),
                                ),
                            ),
                )
            }
        }
    }

    private fun runPairWireless() {
        val host = uiState.ip.trim()
        val pairPort = uiState.pairPort.trim().toIntOrNull()
        val code = uiState.pairCode.trim()
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
        val host = uiState.ip.trim()
        val adbPort = uiState.adbPort.trim().toIntOrNull()
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
        uiState =
            uiState.copy(
                busy = busy,
                installEnabled = !busy && embeddedRepo.embeddedApkAvailable(),
            )
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

    private fun applyOcrParsed(parsed: WearInstallOcrParsed) {
        uiState =
            uiState.copy(
                ip = parsed.ip.orEmpty(),
                pairPort = parsed.pairPort?.toString().orEmpty(),
                pairCode = parsed.pairCode.orEmpty(),
                adbPort = parsed.adbPort?.toString().orEmpty(),
            )
        scrollToDirectToken++
        val filled =
            parsed.ip != null ||
                parsed.pairPort != null ||
                parsed.pairCode != null ||
                parsed.adbPort != null
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

    private fun showDirectStatus(message: String, error: Boolean) {
        uiState =
            uiState.copy(
                statusMessage = message,
                statusIsError = error,
            )
    }
}
