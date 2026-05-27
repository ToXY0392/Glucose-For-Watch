package com.glucoseforwatch.mobile.ui

import android.content.Context
import com.glucoseforwatch.mobile.R

/** Builds localized [HomeSyncPillLabels] from string resources. */
object HomeSyncPillLabelsFactory {
    fun create(context: Context): HomeSyncPillLabels =
        HomeSyncPillLabels(
            dexcomOff = context.getString(R.string.home_status_dexcom_off),
            watchUnreachable = context.getString(R.string.home_status_watch_unreachable),
            watchPushPending = context.getString(R.string.home_status_watch_push_pending),
            watchPending = context.getString(R.string.home_status_watch_pending),
            watchNotPaired = context.getString(R.string.home_status_watch_not_paired),
            watchInstall = context.getString(R.string.home_status_watch_install),
            syncError = { error -> context.getString(R.string.home_status_sync_error, error) },
            watchConfirmed = context.getString(R.string.home_status_watch_confirmed),
            syncActive = context.getString(R.string.home_status_sync_active),
            ready = context.getString(R.string.home_status_ready),
        )
}

/** Text and background colors for each [HomeSyncPillTone]. */
object HomeSyncPillToneColors {
    /** Text and background color resources for a sync pill tone. */
    data class Colors(
        val textColorRes: Int,
        val backgroundColorRes: Int,
    )

    fun forTone(tone: HomeSyncPillTone): Colors =
        when (tone) {
            HomeSyncPillTone.OK ->
                Colors(
                    textColorRes = R.color.wg7_success,
                    backgroundColorRes = R.color.wg7_success_soft,
                )
            HomeSyncPillTone.WARN ->
                Colors(
                    textColorRes = R.color.wg7_accent_dark,
                    backgroundColorRes = R.color.wg7_accent_soft,
                )
            HomeSyncPillTone.ERROR ->
                Colors(
                    textColorRes = R.color.wg7_danger,
                    backgroundColorRes = R.color.wg7_danger_soft,
                )
            HomeSyncPillTone.NEUTRAL ->
                Colors(
                    textColorRes = R.color.wg7_text_secondary,
                    backgroundColorRes = R.color.wg7_neutral_soft,
                )
        }
}
