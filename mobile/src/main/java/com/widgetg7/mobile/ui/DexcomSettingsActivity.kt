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
import kotlinx.coroutines.launch

class DexcomSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dexcom_settings)

        val settingsStore = AppSettingsStore(this)
        val currentSettings = settingsStore.loadDexcomSettings()

        val usernameInput = findViewById<EditText>(R.id.dexcomUsernameInput)
        val passwordInput = findViewById<EditText>(R.id.dexcomPasswordInput)
        val serverInput = findViewById<AutoCompleteTextView>(R.id.dexcomServerInput)
        val statusText = findViewById<TextView>(R.id.dexcomSettingsStatusText)

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

        findViewById<Button>(R.id.saveDexcomButton).setOnClickListener {
            lifecycleScope.launch {
                val settings = DexcomUserSettings(
                    username = usernameInput.text.toString(),
                    password = passwordInput.text.toString(),
                    server = toServerCode(serverInput.text.toString()),
                )

                settingsStore.saveDexcomSettings(settings)
                statusText.text = "Configuration enregistree."
            }
        }

        findViewById<Button>(R.id.testDexcomButton).setOnClickListener {
            lifecycleScope.launch {
                val settings = DexcomUserSettings(
                    username = usernameInput.text.toString(),
                    password = passwordInput.text.toString(),
                    server = toServerCode(serverInput.text.toString()),
                )

                settingsStore.saveDexcomSettings(settings)
                statusText.text = "Connexion Dexcom en cours..."

                try {
                    val config = DexcomShareConfig(
                        username = settings.username,
                        password = settings.password,
                        server = settings.server,
                        applicationId = BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim(),
                    )
                    val reading = DexcomSharePhoneGlucoseSource(config).latest()
                    statusText.text = "Connexion reussie. Derniere valeur: ${reading.valueMgDl} mg/dL"
                } catch (t: Throwable) {
                    statusText.text = "Connexion echouee: ${t.message ?: "erreur inconnue"}"
                }
            }
        }
    }

    private fun toServerCode(label: String): String = if (label.equals("US", true)) "US" else "OUS"

    private fun displayServer(server: String): String = if (server.equals("US", true)) "US" else "Europe"
}
