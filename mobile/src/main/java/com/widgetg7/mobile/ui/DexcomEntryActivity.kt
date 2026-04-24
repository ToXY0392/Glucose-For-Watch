package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.widgetg7.mobile.R
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.LegalConsentStore
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.status.SyncStatusRepository

class DexcomEntryActivity : AppCompatActivity() {
    private lateinit var openDexcomLoginButton: MaterialButton
    private lateinit var backToHomeButton: ImageButton
    private lateinit var legalTermsCheckbox: CheckBox
    private lateinit var medicalWarningCheckbox: CheckBox
    private lateinit var appSettingsStore: AppSettingsStore
    private lateinit var launchStateStore: LaunchStateStore
    private lateinit var syncStatusRepository: SyncStatusRepository
    private lateinit var legalConsentStore: LegalConsentStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dexcom_entry)

        appSettingsStore = AppSettingsStore(this)
        launchStateStore = LaunchStateStore(this)
        syncStatusRepository = SyncStatusRepository(this)
        legalConsentStore = LegalConsentStore(this)
        openDexcomLoginButton = findViewById(R.id.openDexcomLoginButton)
        backToHomeButton = findViewById(R.id.backToHomeButton)
        legalTermsCheckbox = findViewById(R.id.legalTermsCheckbox)
        medicalWarningCheckbox = findViewById(R.id.medicalWarningCheckbox)

        bindLegalLinks(findViewById(R.id.legalLinksText))

        if (legalConsentStore.hasAcceptedCurrentVersion()) {
            legalTermsCheckbox.isChecked = true
            medicalWarningCheckbox.isChecked = true
        }

        val updateButtonState = {
            openDexcomLoginButton.isEnabled = legalTermsCheckbox.isChecked && medicalWarningCheckbox.isChecked
        }

        legalTermsCheckbox.setOnCheckedChangeListener { _, _ -> updateButtonState() }
        medicalWarningCheckbox.setOnCheckedChangeListener { _, _ -> updateButtonState() }
        renderPrimaryAction()

        backToHomeButton.setOnClickListener { finish() }

        openDexcomLoginButton.setOnClickListener {
            if (appSettingsStore.loadDexcomSettings().isConfigured()) {
                showDisconnectConfirmation()
            } else {
                legalConsentStore.markAcceptedCurrentVersion()
                startActivity(
                    Intent(this, DexcomSettingsActivity::class.java).apply {
                        putExtra(DexcomSettingsActivity.EXTRA_FIRST_CONNECTION_FLOW, true)
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        renderPrimaryAction()
    }

    private fun bindLegalLinks(textView: TextView) {
        val text = "Lire les CGU, la Politique de confidentialité et l'Avertissement médical."
        val spannable = SpannableString(text)

        addLink(
            spannable = spannable,
            fullText = text,
            linkText = "CGU",
            documentType = LegalDocumentActivity.DOCUMENT_TYPE_CGU,
        )
        addLink(
            spannable = spannable,
            fullText = text,
            linkText = "Politique de confidentialité",
            documentType = LegalDocumentActivity.DOCUMENT_TYPE_PRIVACY,
        )
        addLink(
            spannable = spannable,
            fullText = text,
            linkText = "Avertissement médical",
            documentType = LegalDocumentActivity.DOCUMENT_TYPE_MEDICAL,
        )

        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = android.graphics.Color.TRANSPARENT
    }

    private fun addLink(
        spannable: SpannableString,
        fullText: String,
        linkText: String,
        documentType: String,
    ) {
        val start = fullText.indexOf(linkText)
        if (start < 0) return
        val end = start + linkText.length
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(
                        Intent(this@DexcomEntryActivity, LegalDocumentActivity::class.java).apply {
                            putExtra(LegalDocumentActivity.EXTRA_DOCUMENT_TYPE, documentType)
                        },
                    )
                }
            },
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
    }

    private fun renderPrimaryAction() {
        val isConfigured = appSettingsStore.loadDexcomSettings().isConfigured()
        openDexcomLoginButton.text = if (isConfigured) "Se déconnecter" else "Se connecter"
        openDexcomLoginButton.isEnabled = if (isConfigured) {
            true
        } else {
            legalTermsCheckbox.isChecked && medicalWarningCheckbox.isChecked
        }
    }

    private fun showDisconnectConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Déconnecter Dexcom")
            .setMessage("Voulez-vous supprimer le compte Dexcom de l'application ?")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Se déconnecter") { _, _ ->
                appSettingsStore.clearDexcomSettings()
                launchStateStore.resetDexcomEntry()
                legalConsentStore.clearAcceptedVersion()
                syncStatusRepository.clearSessionState()
                legalTermsCheckbox.isChecked = false
                medicalWarningCheckbox.isChecked = false
                renderPrimaryAction()
            }
            .show()
    }
}
