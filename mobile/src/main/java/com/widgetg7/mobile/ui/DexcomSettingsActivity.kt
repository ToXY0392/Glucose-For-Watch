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
import com.widgetg7.mobile.dexcom.DexcomShareException
import com.widgetg7.mobile.dexcom.DexcomSharePhoneGlucoseSource
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.status.SyncErrorCategory
import com.widgetg7.mobile.status.SyncStatusRepository
import kotlinx.coroutines.launch

class DexcomSettingsActivity : AppCompatActivity() {

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

        usernameInput.setText(currentSettings.username)
        passwordInput.setText(currentSettings.password)
        serverInput.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                listOf("Europe", "US"),
            ),
        )
        serverInput.setText(displayServer(currentSettings.server), false)

        renderAccountSummary(currentSettings, syncStatusRepository.load(), accountSummaryText, statusText)

        findViewById<Button>(R.id.saveDexcomButton).setOnClickListener {
            lifecycleScope.launch {
                val settings = readSettings(usernameInput, passwordInput, serverInput)
                settingsStore.saveDexcomSettings(settings)
                syncStatusRepository.clearSessionState()
                renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                statusText.text = "Configuration enregistree."
            }
        }

        findViewById<Button>(R.id.testDexcomButton).setOnClickListener {
            lifecycleScope.launch {
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
                    syncStatusRepository.saveError(toUserMessage(t), toCategory(t))
                    renderAccountSummary(settings, syncStatusRepository.load(), accountSummaryText, statusText)
                    statusText.text = "Connexion echouee: ${toUserMessage(t)}"
                }
            }
        }

        findViewById<Button>(R.id.disconnectDexcomButton).setOnClickListener {
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
        accountSummaryText.text = when {
            !settings.isConfigured() -> "Compte Dexcom non configure"
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                "Compte Dexcom configure, reconnexion requise"

            syncStatus.hasSuccessfulSync() -> "Compte Dexcom connecte (${displayServer(settings.server)})"
            else -> "Compte Dexcom configure (${displayServer(settings.server)})"
        }

        if (statusText.text.isNullOrBlank()) {
            statusText.text = "Aucune verification effectuee pour le moment."
        }
    }

    private fun toCategory(t: Throwable): SyncErrorCategory {
        return if (t is DexcomShareException && t.kind == com.widgetg7.mobile.dexcom.DexcomShareErrorKind.AUTH) {
            SyncErrorCategory.AUTH
        } else if (t is DexcomShareException && t.kind == com.widgetg7.mobile.dexcom.DexcomShareErrorKind.NETWORK) {
            SyncErrorCategory.NETWORK
        } else {
            SyncErrorCategory.OTHER
        }
    }

    private fun toUserMessage(t: Throwable): String {
        return if (t is DexcomShareException) t.message else (t.message ?: "Erreur inconnue")
    }

    private fun toServerCode(label: String): String = if (label.equals("US", true)) "US" else "OUS"

    private fun displayServer(server: String): String = if (server.equals("US", true)) "US" else "Europe"
}
