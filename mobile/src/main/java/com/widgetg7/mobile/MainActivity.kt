package com.widgetg7.mobile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.ui.DexcomEntryActivity
import com.widgetg7.mobile.ui.NoticeActivity
import com.widgetg7.mobile.ui.WatchSetupActivity
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchConnectionStatus
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import com.widgetg7.mobile.watch.WatchSyncHealthStatus
import com.widgetg7.mobile.watch.WatchVisualResolver
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val logTag = "WidgetG7Phone"
    private var baseScrollPaddingTop = 0

    private lateinit var watchSettingsButton: ImageButton
    private lateinit var watchPreviewImage: ImageView
    private lateinit var watchModelText: TextView
    private lateinit var watchFaceStatusText: TextView
    private lateinit var watchRefreshButton: ImageButton
    private lateinit var openNoticeButton: TextView
    private lateinit var watchActionRow: LinearLayout

    private var dexcomConnected = false
    private var homeMenuPopup: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainScrollView = findViewById<ScrollView>(R.id.mainScrollView)
        baseScrollPaddingTop = mainScrollView.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(mainScrollView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                baseScrollPaddingTop + systemBarsInsets.top,
                view.paddingRight,
                view.paddingBottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(mainScrollView)

        watchSettingsButton = findViewById(R.id.watchSettingsButton)
        watchPreviewImage = findViewById(R.id.watchPreviewImage)
        watchModelText = findViewById(R.id.watchModelText)
        watchFaceStatusText = findViewById(R.id.watchFaceStatusText)
        watchRefreshButton = findViewById(R.id.watchRefreshButton)
        openNoticeButton = findViewById(R.id.openNoticeButton)
        watchActionRow = findViewById(R.id.watchActionRow)

        watchSettingsButton.setOnClickListener { showHomeMenu() }
        watchRefreshButton.setOnClickListener {
            lifecycleScope.launch { runManualSync() }
        }
        openNoticeButton.setOnClickListener { startActivity(Intent(this, NoticeActivity::class.java)) }

        refreshHome()
    }

    override fun onResume() {
        super.onResume()
        refreshHome()
    }

    private fun refreshHome() {
        dexcomConnected = AppSettingsStore(this).loadDexcomSettings().isConfigured()
        val watchHealth = WatchSyncHealthRepository(this).load()

        lifecycleScope.launch {
            val watchStatus = WatchConnectionRepository(this@MainActivity).loadStatus()
            applyWatchVisuals(watchStatus, watchHealth)
        }
    }

    private fun applyWatchVisuals(
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
    ) {
        val visual = WatchVisualResolver.resolve(watchStatus.displayName, watchHealth)
        watchPreviewImage.setImageResource(visual.drawableResId)
        watchFaceStatusText.text = watchSummary(watchStatus, watchHealth)
        watchModelText.text = if (watchStatus.connected) visual.headline else "Wear OS"
        applyWatchStatusStyle(watchStatus, watchHealth)
    }

    private suspend fun runManualSync() {
        watchRefreshButton.isEnabled = false
        watchRefreshButton.alpha = 0.45f
        watchRefreshButton.contentDescription = "Actualisation en cours"

        when (
            val result = com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine(this).run(
                triggeredFromWatch = false,
                forcePushCurrentReading = true,
            )
        ) {
            is com.widgetg7.mobile.sync.SyncExecutionResult.SuccessNewReading ->
                Log.d(logTag, "Manual sync completed with new reading source=${result.sourceName}")

            is com.widgetg7.mobile.sync.SyncExecutionResult.SuccessNoNewReading ->
                Log.d(
                    logTag,
                    "Manual sync completed without new reading source=${result.sourceName}",
                )

            is com.widgetg7.mobile.sync.SyncExecutionResult.Failure ->
                Log.d(logTag, "Manual sync failed message=${result.message}")
        }

        refreshHome()
        watchRefreshButton.isEnabled = true
        watchRefreshButton.alpha = 1f
        watchRefreshButton.contentDescription = "Actualiser"
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
        val xOffset =
            ((watchActionRow.width - contentView.measuredWidth) / 2) - watchSettingsButton.left
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
            Log.w(logTag, "Impossible d'ouvrir les détails de l'application")
        }
    }

    private fun watchSummary(
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
    ): String =
        when {
            !watchStatus.connected -> "Non connectée"
            watchHealth?.syncLimited == true -> "Sync limitée"
            else -> "Connectée"
        }

    private fun applyWatchStatusStyle(
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
    ) {
        val color = when {
            !watchStatus.connected -> R.color.wg7_danger
            watchHealth?.syncLimited == true -> R.color.wg7_alert
            else -> R.color.wg7_success
        }
        watchFaceStatusText.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun onDestroy() {
        homeMenuPopup?.dismiss()
        homeMenuPopup = null
        super.onDestroy()
    }
}
