package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.widgetg7.mobile.BuildConfig
import com.widgetg7.mobile.MainActivity
import com.widgetg7.mobile.R
import com.widgetg7.mobile.dexcom.DexcomShareConfig
import com.widgetg7.mobile.dexcom.DexcomSharePhoneGlucoseSource
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.settings.LegalConsentStore
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.sync.SyncText
import kotlinx.coroutines.launch

class DexcomSettingsActivity : AppCompatActivity() {
    private lateinit var saveDexcomButton: Button
    private lateinit var disconnectDexcomButton: Button
    private lateinit var backIconButton: ImageView
    private var firstConnectionFlow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!LegalConsentStore(this).hasAcceptedCurrentVersion()) {
            startActivity(
                Intent(this, DexcomEntryActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
            )
            finish()
            return
        }
        setContentView(R.layout.activity_dexcom_settings)
        firstConnectionFlow = intent.getBooleanExtra(EXTRA_FIRST_CONNECTION_FLOW, false)

        val settingsStore = AppSettingsStore(this)
        val launchStateStore = LaunchStateStore(this)
        val syncStatusRepository = SyncStatusRepository(this)
        val currentSettings = settingsStore.loadDexcomSettings()

        val usernameInput = findViewById<EditText>(R.id.dexcomUsernameInput)
        val passwordInput = findViewById<EditText>(R.id.dexcomPasswordInput)
        val serverInput = findViewById<AutoCompleteTextView>(R.id.dexcomServerInput)
        val statusText = findViewById<TextView>(R.id.dexcomSettingsStatusText)
        val accountSummaryText = findViewById<TextView>(R.id.dexcomAccountSummaryText)
        saveDexcomButton = findViewById(R.id.saveDexcomButton)
        disconnectDexcomButton = findViewById(R.id.disconnectDexcomButton)
        backIconButton = findViewById(R.id.backIconButton)

        usernameInput.setText(currentSettings.username)
        passwordInput.setText(currentSettings.password)
        serverInput.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                listOf("Europe", "US"),
            ),
        )
        serverInput.setText(SyncText.displayServer(currentSettings.server), false)

        renderAccountSummary(currentSettings, syncStatusRepository.load(), accountSummaryText, statusText)

        backIconButton.setOnClickListener { finish() }

        saveDexcomButton.setOnClickListener {
            lifecycleScope.launch {
                setBusyState(true)
                val settings = readSettings(usernameInput, passwordInput, serverInput)

                statusText.text = "Connexion Dexcom en cours..."

                try {
                    val config = DexcomShareConfig(
                        username = settings.username,
                        password = settings.password,
                        server = settings.server,
                        applicationId = BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim(),
                    )
                    val reading = DexcomSharePhoneGlucoseSource(config).latest()
                    settingsStore.saveDexcomSettings(settings)
                    launchStateStore.markDexcomEntryCompleted()
                    syncStatusRepository.saveSuccess("dexcom-share", reading)
                    renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                    statusText.text = "Connexion réussie."
                    Snackbar.make(findViewById(android.R.id.content), "Connexion réussie", 1000).show()
                    if (firstConnectionFlow) {
                        startActivity(
                            Intent(this@DexcomSettingsActivity, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            },
                        )
                        finish()
                    }
                } catch (t: Throwable) {
                    syncStatusRepository.saveError(SyncText.toUserMessage(t), SyncText.toCategory(t))
                    renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                    statusText.text = "Connexion échouée : ${SyncText.toUserMessage(t)}"
                }

                setBusyState(false)
            }
        }

        disconnectDexcomButton.setOnClickListener {
            settingsStore.clearDexcomSettings()
            launchStateStore.resetDexcomEntry()
            LegalConsentStore(this).clearAcceptedVersion()
            syncStatusRepository.clearSessionState()
            startActivity(
                Intent(this, DexcomEntryActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                },
            )
            finish()
        }
    }

    private fun readSettings(
        usernameInput: EditText,
        passwordInput: EditText,
        serverInput: AutoCompleteTextView,
    ): DexcomUserSettings {
        return DexcomUserSettings(
            username = usernameInput.text.toString(),
            password = passwordInput.text.toString(),
            server = toServerCode(serverInput.text.toString()),
        )
    }

    private fun renderAccountSummary(
        settings: DexcomUserSettings,
        syncStatus: com.widgetg7.mobile.status.SyncStatusSnapshot,
        accountSummaryText: TextView,
        statusText: TextView,
    ) {
        accountSummaryText.text = SyncText.dexcomAccountSummary(settings, syncStatus)

        if (statusText.text.isNullOrBlank()) {
            statusText.text = "Aucune vérification n'a encore été effectuée."
        }
    }

    private fun setBusyState(isBusy: Boolean) {
        saveDexcomButton.isEnabled = !isBusy
        disconnectDexcomButton.isEnabled = !isBusy
        saveDexcomButton.text = if (isBusy) "Enregistrement..." else "Enregistrer"
    }

    private fun toServerCode(label: String): String = if (label.equals("US", true)) "US" else "OUS"

    companion object {
        const val EXTRA_FIRST_CONNECTION_FLOW = "extra_first_connection_flow"
    }
}
