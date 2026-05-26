package com.widgetg7.mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.widgetg7.mobile.settings.LaunchStateStore
import com.widgetg7.mobile.ui.DexcomEntryActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Routes to Dexcom entry or home on cold start. */
class SplashActivity : AppCompatActivity() {
    private val splashDurationMs = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(splashDurationMs)
            val targetActivity =
                if (LaunchStateStore(this@SplashActivity).hasCompletedDexcomEntry()) {
                    MainActivity::class.java
                } else {
                    DexcomEntryActivity::class.java
                }
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
