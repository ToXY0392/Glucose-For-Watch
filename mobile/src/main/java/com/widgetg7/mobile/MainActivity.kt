package com.widgetg7.mobile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.settings.LegalConsentStore
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.status.SyncStatusSnapshot
import com.widgetg7.mobile.sync.ActiveGlucoseSyncController
import com.widgetg7.mobile.sync.PhoneAutoSyncScheduler
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import com.widgetg7.mobile.ui.DexcomEntryActivity
import com.widgetg7.mobile.ui.DexcomSettingsActivity
import com.widgetg7.mobile.ui.GlucoseDisplayFormatter
import com.widgetg7.mobile.ui.NoticeActivity
import com.widgetg7.mobile.ui.WatchSetupActivity
import com.widgetg7.mobile.ui.WearInstallerActivity
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchConnectionStatus
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import com.widgetg7.mobile.watch.WatchSyncHealthStatus
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var baseScrollPaddingTop = 0
    private var baseScrollPaddingBottom = 0

    private lateinit var syncNowButton: ImageButton
    private lateinit var watchSettingsButton: ImageButton
    private lateinit var homeReadingPrimary: TextView
    private lateinit var homeReadingSubtitle: TextView
    private lateinit var homeStatusText: TextView
    private lateinit var dexcomCardStatus: TextView
    private lateinit var dexcomDisconnectLabel: TextView
    private lateinit var dexcomCardChevron: ImageView
    private lateinit var installChevron: ImageView
    private lateinit var wearAssistantChevron: ImageView
    private lateinit var openNoticeButton: TextView
    private lateinit var watchActionRow: LinearLayout

    private var homeMenuPopup: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainScrollView = findViewById<NestedScrollView>(R.id.mainScrollView)
        baseScrollPaddingTop = mainScrollView.paddingTop
        baseScrollPaddingBottom = mainScrollView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(mainScrollView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                baseScrollPaddingTop + systemBarsInsets.top,
                view.paddingRight,
                baseScrollPaddingBottom + systemBarsInsets.bottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(mainScrollView)

        syncNowButton = findViewById(R.id.syncNowButton)
        watchSettingsButton = findViewById(R.id.watchSettingsButton)
        findViewById<View>(R.id.installCard).setOnClickListener {
            startActivity(Intent(this, WatchSetupActivity::class.java))
        }
        findViewById<View>(R.id.wearAssistantCard).setOnClickListener {
            startActivity(Intent(this, WearInstallerActivity::class.java))
        }

        val dexcomCard = findViewById<View>(R.id.dexcomCard)
        dexcomCard.setOnClickListener {
            val settings = AppSettingsStore(this).loadDexcomSettings()
            if (settings.isConfigured()) {
                startActivity(Intent(this, DexcomSettingsActivity::class.java))
            } else {
                startActivity(Intent(this, DexcomEntryActivity::class.java))
            }
        }

        homeReadingPrimary = findViewById(R.id.homeReadingPrimary)
        homeReadingSubtitle = findViewById(R.id.homeReadingSubtitle)
        homeStatusText = findViewById(R.id.homeStatusText)
        dexcomCardStatus = findViewById(R.id.dexcomCardStatus)
        dexcomDisconnectLabel = findViewById(R.id.dexcomDisconnectLabel)
        dexcomCardChevron = findViewById(R.id.dexcomCardChevron)
        installChevron = findViewById(R.id.installChevron)
        wearAssistantChevron = findViewById(R.id.wearAssistantChevron)
        openNoticeButton = findViewById(R.id.openNoticeButton)
        watchActionRow = findViewById(R.id.watchActionRow)

        dexcomDisconnectLabel.setOnClickListener {
            showDexcomDisconnectConfirmation()
        }

        syncNowButton.setOnClickListener {
            lifecycleScope.launch { runManualSync() }
        }

        watchSettingsButton.setOnClickListener { showHomeMenu() }
        openNoticeButton.setOnClickListener { startActivity(Intent(this, NoticeActivity::class.java)) }

        scheduleAutoSyncIfReady()
        refreshHome()
    }

    override fun onResume() {
        super.onResume()
        scheduleAutoSyncIfReady()
        refreshHome()
    }

    private fun refreshHome() {
        val settings = AppSettingsStore(this).loadDexcomSettings()
        val dexcomConfigured = settings.isConfigured()
        updateDexcomCard(settings)
        val watchHealth = WatchSyncHealthRepository(this).load()
        val syncStatus = SyncStatusRepository(this).load()
        updateSyncButtonTint(dexcomConfigured, syncStatus)

        lifecycleScope.launch {
            val watchStatus = WatchConnectionRepository(this@MainActivity).loadStatus()
            updateHomeHero(dexcomConfigured, syncStatus, watchStatus, watchHealth)
            updateStepChevrons(dexcomConfigured, syncStatus, watchStatus, watchHealth)
        }
    }

    private fun updateDexcomCard(settings: DexcomUserSettings) {
        if (settings.isConfigured()) {
            val username = settings.username.trim()
            dexcomCardStatus.text =
                if (username.isNotEmpty()) {
                    "${getString(R.string.home_dexcom_status_on)} · ${getString(R.string.home_dexcom_user_mask, username.take(3))}"
                } else {
                    getString(R.string.home_dexcom_status_on)
                }
            dexcomDisconnectLabel.visibility = View.VISIBLE
            dexcomCardChevron.visibility = View.GONE
        } else {
            dexcomCardStatus.text = getString(R.string.home_dexcom_status_off)
            dexcomDisconnectLabel.visibility = View.GONE
            dexcomCardChevron.visibility = View.VISIBLE
        }
    }

    private fun updateHomeHero(
        dexcomConfigured: Boolean,
        syncStatus: SyncStatusSnapshot,
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
    ) {
        val activeSync = AppSettingsStore(this).isActiveSyncEnabled()
        val watchReady = watchHealth?.appInstalled == true && !(watchHealth.syncLimited)
        val primary = GlucoseDisplayFormatter.homeValuePrimary(syncStatus)
        val subtitle = GlucoseDisplayFormatter.homeValueSubtitle(syncStatus)

        when {
            primary != null -> {
                homeReadingPrimary.text = primary
                homeReadingPrimary.setTextColor(ContextCompat.getColor(this, R.color.wg7_text_primary))
                if (!subtitle.isNullOrBlank()) {
                    homeReadingSubtitle.text = subtitle
                    homeReadingSubtitle.visibility = android.view.View.VISIBLE
                } else {
                    homeReadingSubtitle.text = ""
                    homeReadingSubtitle.visibility = android.view.View.GONE
                }
            }
            !dexcomConfigured -> {
                homeReadingPrimary.text = getString(R.string.home_hero_connect)
                homeReadingPrimary.setTextColor(ContextCompat.getColor(this, R.color.wg7_text_primary))
                homeReadingSubtitle.text = getString(R.string.home_hero_connect_hint)
                homeReadingSubtitle.visibility = android.view.View.VISIBLE
            }
            else -> {
                homeReadingPrimary.text = getString(R.string.home_hero_waiting)
                homeReadingPrimary.setTextColor(ContextCompat.getColor(this, R.color.wg7_text_secondary))
                homeReadingSubtitle.visibility = android.view.View.GONE
            }
        }

        val statusLine =
            when {
                !dexcomConfigured -> "Dexcom : non connecté"
                !watchStatus.connected -> "Montre : non associée (facultatif)"
                !watchReady -> "Installez Widget G7 sur la montre"
                syncStatus.lastError.isNotBlank() ->
                    "Sync : ${truncateForStatusPill(syncStatus.lastError)}"
                hasWatchAck() && activeSync -> "Liaison montre confirmée"
                activeSync && syncStatus.hasSuccessfulSync() -> "Synchronisation active"
                else -> "Prêt"
            }
        homeStatusText.text = statusLine
    }

    private fun truncateForStatusPill(raw: String, maxLen: Int = 30): String {
        val t = raw.trim()
        if (t.length <= maxLen) return t
        return t.take(maxLen - 1).trimEnd { !it.isLetterOrDigit() } + "…"
    }

    private fun updateStepChevrons(
        dexcomConfigured: Boolean,
        syncStatus: SyncStatusSnapshot,
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
    ) {
        fun tintChevron(view: ImageView, ok: Boolean) {
            view.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        if (ok) R.color.wg7_accent else R.color.wg7_text_secondary,
                    ),
                )
        }
        val installOk = watchHealth?.appInstalled == true && !watchHealth.syncLimited
        val montreChevronOk = installOk && hasWatchAck()
        tintChevron(installChevron, montreChevronOk)
        tintChevron(wearAssistantChevron, installOk)
    }

    private fun updateSyncButtonTint(dexcomConfigured: Boolean, syncStatus: SyncStatusSnapshot) {
        val ready = dexcomConfigured && syncStatus.lastError.isBlank()
        syncNowButton.imageTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    if (ready) R.color.wg7_accent_dark else R.color.wg7_text_secondary,
                ),
            )
    }

    private suspend fun runManualSync() {
        syncNowButton.isEnabled = false
        syncNowButton.alpha = 0.45f

        ActiveGlucoseSyncController.syncNow(this)

        refreshHome()
        syncNowButton.isEnabled = true
        syncNowButton.alpha = 1f
        Snackbar.make(
            findViewById(android.R.id.content),
            "Sync demandee : le telephone pousse vers la montre puis verifie l'ack.",
            Snackbar.LENGTH_LONG,
        ).show()
    }

    private fun showDexcomDisconnectConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.home_dexcom_disconnect_title)
            .setMessage(R.string.home_dexcom_disconnect_message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.home_dexcom_disconnect) { _, _ ->
                AppSettingsStore(this).clearDexcomSettings()
                ActiveGlucoseSyncController.stop(this)
                LaunchStateStore(this).resetDexcomEntry()
                LegalConsentStore(this).clearAcceptedVersion()
                SyncStatusRepository(this).clearSessionState()
                PhoneSyncStateStore(this).clear()
                refreshHome()
            }
            .show()
    }

    private fun scheduleAutoSyncIfReady() {
        val settingsStore = AppSettingsStore(this)
        if (settingsStore.loadDexcomSettings().isConfigured()) {
            if (settingsStore.isActiveSyncEnabled()) {
                ActiveGlucoseSyncController.start(this)
            }
            PhoneAutoSyncScheduler.schedule(this, delayMs = 5_000L)
        }
    }

    private fun hasWatchAck(): Boolean {
        val state = PhoneSyncStateStore(this).load()
        return state.lastAckSequenceId == state.lastPushSequenceId && state.lastAckSequenceId > 0L
    }

    private fun showHomeMenu() {
        homeMenuPopup?.let { existing ->
            if (existing.isShowing) {
                existing.dismiss()
                return
            }
        }

        val contentView = LayoutInflater.from(this).inflate(R.layout.popup_home_menu, null)
        val popup =
            PopupWindow(
                contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true,
            )

        popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup.isOutsideTouchable = true
        popup.elevation = 0f
        popup.animationStyle = R.style.AnimationWidgetG7HomeMenu
        popup.setOnDismissListener { homeMenuPopup = null }

        contentView.findViewById<TextView>(R.id.menuWatchSettings).setOnClickListener {
            popup.dismiss()
            startActivity(Intent(this, WatchSetupActivity::class.java))
        }

        contentView.findViewById<TextView>(R.id.menuDexcom).setOnClickListener {
            popup.dismiss()
            startActivity(Intent(this, DexcomEntryActivity::class.java))
        }

        contentView.findViewById<TextView>(R.id.menuPermissions).setOnClickListener {
            popup.dismiss()
            openAppDetailsSettings()
        }

        contentView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        )
        val xOffset = watchSettingsButton.width - contentView.measuredWidth
        val yOffset = (10 * resources.displayMetrics.density).toInt()

        popup.showAsDropDown(watchSettingsButton, xOffset, yOffset)
        homeMenuPopup = popup
    }

    private fun openAppDetailsSettings() {
        val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }

        try {
            startActivity(appDetailsIntent)
        } catch (_: ActivityNotFoundException) {
            // No settings activity available on this device.
        }
    }

    override fun onDestroy() {
        homeMenuPopup?.dismiss()
        homeMenuPopup = null
        super.onDestroy()
    }
}
