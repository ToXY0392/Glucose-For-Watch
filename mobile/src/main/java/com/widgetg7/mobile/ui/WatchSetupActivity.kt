package com.widgetg7.mobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.R
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import kotlinx.coroutines.launch

class WatchSetupActivity : AppCompatActivity() {
    private lateinit var refreshWatchStatusButton: Button
    private lateinit var watchStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_setup)

        refreshWatchStatusButton = findViewById(R.id.refreshWatchStatusButton)
        watchStatusText = findViewById(R.id.watchSetupStatusText)

        refreshWatchStatusButton.setOnClickListener {
            refreshWatchStatus()
        }

        refreshWatchStatus()
    }

    override fun onResume() {
        super.onResume()
        refreshWatchStatus()
    }

    private fun refreshWatchStatus() {
        refreshWatchStatusButton.isEnabled = false
        refreshWatchStatusButton.text = "Vérification..."
        watchStatusText.text = "Vérification de la montre..."
        lifecycleScope.launch {
            val status = WatchConnectionRepository(this@WatchSetupActivity).loadStatus()
            val watchHealth = WatchSyncHealthRepository(this@WatchSetupActivity).load()
            val summary = watchHealth?.summary()
            watchStatusText.text = if (summary.isNullOrBlank()) {
                status.label()
            } else {
                "${status.label()}\n$summary"
            }
            refreshWatchStatusButton.isEnabled = true
            refreshWatchStatusButton.text = "Vérifier la connexion montre"
        }
    }
}
