package com.widgetg7.mobile

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.sync.PhoneAutoSyncScheduler
import com.widgetg7.mobile.sync.PhoneWearSyncService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val logTag = "WidgetG7Phone"

    private val source by lazy { PhoneGlucoseSourceFactory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PhoneAutoSyncScheduler.schedule(this)

        val statusText = findViewById<TextView>(R.id.statusText)
        statusText.text = "Source active: ${source.sourceName} - auto sync 2 min"
        val syncNowButton = findViewById<Button>(R.id.syncNowButton)
        syncNowButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    Log.d(logTag, "Manual sync started with source=${source.sourceName}")
                    val reading = source.latest()
                    Log.d(
                        logTag,
                        "Manual sync fetched value=${reading.valueMgDl} trend=${reading.trend} delta=${reading.deltaMgDl} stale=${reading.stale}",
                    )
                    PhoneWearSyncService(this@MainActivity).pushLatest(reading)
                    Log.d(logTag, "Manual sync push completed")
                    statusText.text =
                        "Source ${source.sourceName}: ${reading.valueMgDl} mg/dL ${reading.trend} ${signed(reading.deltaMgDl)}"
                } catch (t: Throwable) {
                    Log.e(logTag, "Manual sync failed", t)
                    statusText.text = "Echec sync ${source.sourceName}: ${t.message ?: "erreur inconnue"}"
                }
            }
        }
    }

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()
}
