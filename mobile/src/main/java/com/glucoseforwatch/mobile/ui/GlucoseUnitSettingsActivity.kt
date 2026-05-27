package com.glucoseforwatch.mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.mobile.settings.DisplaySettingsStore
import com.glucoseforwatch.mobile.sync.PhoneGlucoseSyncEngine
import com.glucoseforwatch.mobile.ui.compose.GlucoseUnitSettingsScreen
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme
import kotlinx.coroutines.launch

/** Lets the user pick mg/dL or mmol/L for phone + watch display. */
class GlucoseUnitSettingsActivity : ComponentActivity() {
    private lateinit var displaySettings: DisplaySettingsStore
    private var selectedUnit by mutableStateOf(GlucoseDisplayUnit.MG_DL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displaySettings = DisplaySettingsStore(this)
        selectedUnit = displaySettings.loadGlucoseDisplayUnit()

        enableEdgeToEdge()
        setContent {
            GlucoseForWatchTheme {
                GlucoseUnitSettingsScreen(
                    selectedUnit = selectedUnit,
                    onUnitSelected = { unit ->
                        if (unit == selectedUnit) return@GlucoseUnitSettingsScreen
                        selectedUnit = unit
                        displaySettings.saveGlucoseDisplayUnit(unit)
                        lifecycleScope.launch {
                            PhoneGlucoseSyncEngine(this@GlucoseUnitSettingsActivity).run(
                                triggeredFromWatch = false,
                                forcePushCurrentReading = true,
                            )
                        }
                    },
                    onBack = { finish() },
                )
            }
        }
    }
}
