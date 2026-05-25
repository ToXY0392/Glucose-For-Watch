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
import com.widgetg7.feature.sync.SyncStatusRepository
import com.widgetg7.mobile.R
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.LegalConsentStore
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.sync.ActiveGlucoseSyncController
import com.widgetg7.mobile.sync.PhoneSyncStateStore

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
        val text = getString(R.string.dexcom_entry_legal_links)
        val spannable = SpannableString(text)

        addLink(
            spannable = spannable,
            fullText = text,
            linkText = getString(R.string.dexcom_entry_link_cgu),
            documentType = LegalDocumentActivity.DOCUMENT_TYPE_CGU,
        )
        addLink(
            spannable = spannable,
            fullText = text,
            linkText = getString(R.string.dexcom_entry_link_privacy),
            documentType = LegalDocumentActivity.DOCUMENT_TYPE_PRIVACY,
        )
        addLink(
            spannable = spannable,
            fullText = text,
            linkText = getString(R.string.dexcom_entry_link_medical),
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
        openDexcomLoginButton.text = if (isConfigured) {
            getString(R.string.dexcom_entry_disconnect)
        } else {
            getString(R.string.dexcom_entry_connect)
        }
        openDexcomLoginButton.isEnabled = if (isConfigured) {
            true
        } else {
            legalTermsCheckbox.isChecked && medicalWarningCheckbox.isChecked
        }
    }

    private fun showDisconnectConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.home_dexcom_disconnect_title)
            .setMessage(R.string.dexcom_entry_disconnect_message)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.dexcom_entry_disconnect) { _, _ ->
                appSettingsStore.clearDexcomSettings()
                ActiveGlucoseSyncController.stop(this)
                launchStateStore.resetDexcomEntry()
                legalConsentStore.clearAcceptedVersion()
                syncStatusRepository.clearSessionState()
                PhoneSyncStateStore(this).clear()
                legalTermsCheckbox.isChecked = false
                medicalWarningCheckbox.isChecked = false
                renderPrimaryAction()
            }
            .show()
    }
}
