package com.glucoseforwatch.mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.glucoseforwatch.mobile.ui.compose.MedicalWarningScreen
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

/** Displays the medical disclaimer. */
class MedicalWarningActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                MedicalWarningScreen(onBackClick = { finish() })
            }
        }
    }
}
