package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.widgetg7.mobile.R
import com.widgetg7.mobile.battery.BatteryOptimizationHelper
import com.widgetg7.mobile.watch.ConnectedWatchNode
import com.widgetg7.mobile.watch.WatchConnectionRepository
import kotlinx.coroutines.launch

class WatchSetupActivity : AppCompatActivity() {
    private var baseScrollPaddingTop = 0

    private lateinit var batteryOptimizationButton: MaterialButton
    private lateinit var backToHomeButton: ImageButton
    private lateinit var watchSelectorLabel: TextView
    private lateinit var watchSelectorSpinner: Spinner

    private var connectedWatches: List<ConnectedWatchNode> = emptyList()
    private var applyingSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_setup)

        val scrollView = findViewById<ScrollView>(R.id.watchSetupScrollView)
        baseScrollPaddingTop = scrollView.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                baseScrollPaddingTop + systemBarsInsets.top,
                view.paddingRight,
                view.paddingBottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(scrollView)

        batteryOptimizationButton = findViewById(R.id.batteryOptimizationButton)
        backToHomeButton = findViewById(R.id.backToHomeButton)
        watchSelectorLabel = findViewById(R.id.watchSelectorLabel)
        watchSelectorSpinner = findViewById(R.id.watchSelectorSpinner)

        batteryOptimizationButton.setOnClickListener {
            runCatching {
                startActivity(BatteryOptimizationHelper(this).buildSettingsIntent())
            }
        }
        backToHomeButton.setOnClickListener { finish() }

        watchSelectorSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (applyingSelection) return
                    val selectedWatch = connectedWatches.getOrNull(position) ?: return
                    WatchConnectionRepository(this@WatchSetupActivity).savePreferredWatch(selectedWatch)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }

        refreshOptions()
    }

    override fun onResume() {
        super.onResume()
        refreshOptions()
    }

    private fun refreshOptions() {
        lifecycleScope.launch {
            refreshWatchChoices()
            refreshBatteryOptimizationStatus()
        }
    }

    private fun refreshBatteryOptimizationStatus() {
        val status = BatteryOptimizationHelper(this).loadStatus()
        batteryOptimizationButton.text =
            if (status.isProtectedFromOptimization) {
                getString(R.string.watch_setup_battery_ok)
            } else {
                getString(R.string.watch_setup_battery_prompt)
            }
        batteryOptimizationButton.isEnabled = !status.isProtectedFromOptimization
    }

    private suspend fun refreshWatchChoices() {
        val repository = WatchConnectionRepository(this)
        connectedWatches = repository.loadConnectedWatches()

        val showSelector = connectedWatches.size > 1
        watchSelectorLabel.visibility = if (showSelector) View.VISIBLE else View.GONE
        watchSelectorSpinner.visibility = if (showSelector) View.VISIBLE else View.GONE

        if (!showSelector) {
            return
        }

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                connectedWatches.map { it.displayName },
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        watchSelectorSpinner.adapter = adapter

        val preferredNodeId = repository.loadPreferredWatchId()
        val selectedIndex =
            connectedWatches.indexOfFirst { it.nodeId == preferredNodeId }.takeIf { it >= 0 } ?: 0

        applyingSelection = true
        watchSelectorSpinner.setSelection(selectedIndex, false)
        applyingSelection = false
    }
}
