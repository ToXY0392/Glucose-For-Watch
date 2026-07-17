package com.glucoseforwatch.mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.glucoseforwatch.mobile.ui.compose.CguScreen
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

/** Displays terms of use. */
class CguActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                CguScreen(onBackClick = { finish() })
            }
        }
    }
}
