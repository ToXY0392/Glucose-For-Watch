package com.glucoseforwatch.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.glucoseforwatch.mobile.settings.LaunchStateStore
import com.glucoseforwatch.mobile.ui.DexcomEntryActivity

/** Routes to Dexcom entry or home on cold start. */
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val targetActivity =
            if (LaunchStateStore(this).hasCompletedDexcomEntry()) {
                MainActivity::class.java
            } else {
                DexcomEntryActivity::class.java
            }
        startActivity(
            Intent(this, targetActivity).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            },
        )
        overridePendingTransition(0, 0)
        finish()
    }
}
