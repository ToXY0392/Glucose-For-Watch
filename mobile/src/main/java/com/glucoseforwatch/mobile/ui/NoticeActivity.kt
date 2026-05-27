package com.glucoseforwatch.mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.glucoseforwatch.mobile.ui.compose.NoticeScreen
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

/** Displays the in-app user notice from raw assets. */
class NoticeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                NoticeScreen(
                    onBack = { finish() },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                )
            }
        }
    }
}
