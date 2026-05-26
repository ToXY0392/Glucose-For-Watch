package com.widgetg7.mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.widgetg7.core.model.GlucoseDisplayUnit
import com.widgetg7.mobile.settings.DisplaySettingsStore
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.ui.compose.GlucoseUnitSettingsScreen
import com.widgetg7.mobile.ui.theme.WidgetG7Theme
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
            WidgetG7Theme {
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
