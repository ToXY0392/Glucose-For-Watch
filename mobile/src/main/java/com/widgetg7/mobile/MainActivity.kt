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
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.status.SyncStatusSnapshot
import com.widgetg7.mobile.sync.ActiveGlucoseSyncController
import com.widgetg7.mobile.sync.PhoneAutoSyncScheduler
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import com.widgetg7.mobile.ui.DexcomEntryActivity
import com.widgetg7.mobile.ui.NoticeActivity
import com.widgetg7.mobile.ui.WatchSetupActivity
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchConnectionStatus
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import com.widgetg7.mobile.watch.WatchSyncHealthStatus
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var baseScrollPaddingTop = 0
    private var baseScrollPaddingBottom = 0

    private lateinit var watchSettingsButton: ImageButton
    private lateinit var homeStatusPill: TextView
    private lateinit var installChevron: ImageView
    private lateinit var syncChevron: ImageView
    private lateinit var ackChevron: ImageView
    private lateinit var watchRefreshButton: MaterialButton
    private lateinit var openNoticeButton: TextView
    private lateinit var watchActionRow: LinearLayout

    private var homeMenuPopup: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainScrollView = findViewById<ScrollView>(R.id.mainScrollView)
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

        watchSettingsButton = findViewById(R.id.watchSettingsButton)
        homeStatusPill = findViewById(R.id.homeStatusPill)
        installChevron = findViewById(R.id.installChevron)
        syncChevron = findViewById(R.id.syncChevron)
        ackChevron = findViewById(R.id.ackChevron)
        watchRefreshButton = findViewById(R.id.watchRefreshButton)
        openNoticeButton = findViewById(R.id.openNoticeButton)
        watchActionRow = findViewById(R.id.watchActionRow)

        watchSettingsButton.setOnClickListener { showHomeMenu() }
        watchRefreshButton.setOnClickListener {
            lifecycleScope.launch { runManualSync() }
        }
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
        val dexcomConfigured = AppSettingsStore(this).loadDexcomSettings().isConfigured()
        val watchHealth = WatchSyncHealthRepository(this).load()
        val syncStatus = SyncStatusRepository(this).load()

        lifecycleScope.launch {
            val watchStatus = WatchConnectionRepository(this@MainActivity).loadStatus()
            updateHomeStatusPill(dexcomConfigured, syncStatus, watchStatus, watchHealth)
            updateStepChevrons(dexcomConfigured, syncStatus, watchStatus, watchHealth)
        }
    }

    private fun updateHomeStatusPill(
        dexcomConfigured: Boolean,
        syncStatus: SyncStatusSnapshot,
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
    ) {
        val activeSync = AppSettingsStore(this).isActiveSyncEnabled()
        val watchReady = watchHealth?.appInstalled == true && !(watchHealth.syncLimited)
        val pillText =
            when {
                !dexcomConfigured -> "Dexcom · menu"
                !watchStatus.connected -> "Pas de montre"
                !watchReady -> "Installer app Wear"
                syncStatus.lastError.isNotBlank() ->
                    "Sync · ${truncateForStatusPill(syncStatus.lastError)}"
                hasWatchAck() && activeSync -> "OK · confirmé"
                activeSync && syncStatus.hasSuccessfulSync() -> "Sync active"
                else -> "Prêt · sync"
            }
        homeStatusPill.text = pillText
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
        tintChevron(installChevron, installOk)
        tintChevron(syncChevron, dexcomConfigured && syncStatus.lastError.isBlank())
        tintChevron(ackChevron, hasWatchAck())
    }

    private suspend fun runManualSync() {
        watchRefreshButton.isEnabled = false
        watchRefreshButton.alpha = 0.45f
        watchRefreshButton.text = "Sync..."
        watchRefreshButton.contentDescription = "Actualisation en cours"

        ActiveGlucoseSyncController.syncNow(this)

        refreshHome()
        watchRefreshButton.isEnabled = true
        watchRefreshButton.alpha = 1f
        watchRefreshButton.text = "Synchroniser"
        watchRefreshButton.contentDescription = "Actualiser"
        Snackbar.make(
            findViewById(android.R.id.content),
            "Sync demandee : le telephone pousse vers la montre puis verifie l'ack.",
            Snackbar.LENGTH_LONG,
        ).show()
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
