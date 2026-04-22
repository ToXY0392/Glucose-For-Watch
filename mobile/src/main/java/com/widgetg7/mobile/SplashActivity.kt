package com.widgetg7.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.ui.DexcomEntryActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val logTag = "WidgetG7Splash"
    private val splashDurationMs = 1800L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(splashDurationMs)
            val targetActivity =
                if (LaunchStateStore(this@SplashActivity).hasCompletedDexcomEntry()) {
                    MainActivity::class.java
                } else {
                    DexcomEntryActivity::class.java
                }
            Log.d(logTag, "launch target=${targetActivity.simpleName}")
            startActivity(
                Intent(this@SplashActivity, targetActivity).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
            )
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
