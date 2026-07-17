package com.glucoseforwatch.mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.BuildConfig
import com.glucoseforwatch.mobile.ui.compose.AboutScreen
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

/** Displays app information and version. */
class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                AboutScreen(
                    onBackClick = { finish() },
                    versionLabel = getString(R.string.about_version_label, BuildConfig.VERSION_NAME),
                )
            }
        }
    }
}
