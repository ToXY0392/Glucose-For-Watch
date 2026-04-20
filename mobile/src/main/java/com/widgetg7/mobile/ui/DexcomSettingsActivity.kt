package com.widgetg7.mobile.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.BuildConfig
import com.widgetg7.mobile.R
import com.widgetg7.mobile.dexcom.DexcomShareConfig
import com.widgetg7.mobile.dexcom.DexcomSharePhoneGlucoseSource
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.sync.SyncText
import kotlinx.coroutines.launch

class DexcomSettingsActivity : AppCompatActivity() {
    private lateinit var saveDexcomButton: Button
    private lateinit var testDexcomButton: Button
    private lateinit var disconnectDexcomButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dexcom_settings)

        val settingsStore = AppSettingsStore(this)
        val syncStatusRepository = SyncStatusRepository(this)
        val currentSettings = settingsStore.loadDexcomSettings()

        val usernameInput = findViewById<EditText>(R.id.dexcomUsernameInput)
        val passwordInput = findViewById<EditText>(R.id.dexcomPasswordInput)
        val serverInput = findViewById<AutoCompleteTextView>(R.id.dexcomServerInput)
        val statusText = findViewById<TextView>(R.id.dexcomSettingsStatusText)
        val accountSummaryText = findViewById<TextView>(R.id.dexcomAccountSummaryText)
        saveDexcomButton = findViewById(R.id.saveDexcomButton)
        testDexcomButton = findViewById(R.id.testDexcomButton)
        disconnectDexcomButton = findViewById(R.id.disconnectDexcomButton)

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

        saveDexcomButton.setOnClickListener {
            lifecycleScope.launch {
                setBusyState(true)
                val settings = readSettings(usernameInput, passwordInput, serverInput)
                settingsStore.saveDexcomSettings(settings)
                syncStatusRepository.clearSessionState()
                renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                statusText.text = "Configuration enregistree."
                setBusyState(false)
            }
        }

        testDexcomButton.setOnClickListener {
            lifecycleScope.launch {
                setBusyState(true)
                val settings = readSettings(usernameInput, passwordInput, serverInput)

                settingsStore.saveDexcomSettings(settings)
                syncStatusRepository.clearSessionState()
                statusText.text = "Connexion Dexcom en cours..."

                try {
                    val config = DexcomShareConfig(
                        username = settings.username,
                        password = settings.password,
                        server = settings.server,
                        applicationId = BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim(),
                    )
                    val reading = DexcomSharePhoneGlucoseSource(config).latest()
                    syncStatusRepository.saveSuccess("dexcom-share", reading)
                    renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                    statusText.text = "Connexion reussie. Derniere valeur: ${reading.valueMgDl} mg/dL"
                } catch (t: Throwable) {
                    syncStatusRepository.saveError(SyncText.toUserMessage(t), SyncText.toCategory(t))
                    renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                    statusText.text = "Connexion echouee: ${SyncText.toUserMessage(t)}"
                } finally {
                    setBusyState(false)
                }
            }
        }

        disconnectDexcomButton.setOnClickListener {
            settingsStore.clearDexcomSettings()
            syncStatusRepository.clearSessionState()
            usernameInput.setText("")
            passwordInput.setText("")
            serverInput.setText("Europe", false)
            renderAccountSummary(settingsStore.loadDexcomSettings(), syncStatusRepository.load(), accountSummaryText, statusText)
            statusText.text = "Compte Dexcom supprime de l'app."
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
            statusText.text = "Aucune verification effectuee pour le moment."
        }
    }

    private fun setBusyState(isBusy: Boolean) {
        testDexcomButton.isEnabled = !isBusy
        saveDexcomButton.isEnabled = !isBusy
        disconnectDexcomButton.isEnabled = !isBusy
        testDexcomButton.text = if (isBusy) "Connexion..." else "Tester la connexion"
    }

    private fun toServerCode(label: String): String = if (label.equals("US", true)) "US" else "OUS"
}
