package com.widgetg7.mobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.R
import com.widgetg7.mobile.watch.WatchConnectionRepository
import kotlinx.coroutines.launch

class WatchSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_setup)

        findViewById<Button>(R.id.refreshWatchStatusButton).setOnClickListener {
            refreshWatchStatus()
        }

        refreshWatchStatus()
    }

    override fun onResume() {
        super.onResume()
        refreshWatchStatus()
    }

    private fun refreshWatchStatus() {
        val watchStatusText = findViewById<TextView>(R.id.watchSetupStatusText)

        lifecycleScope.launch {
            val status = WatchConnectionRepository(this@WatchSetupActivity).loadStatus()
            watchStatusText.text = status.label()
        }
    }
}
