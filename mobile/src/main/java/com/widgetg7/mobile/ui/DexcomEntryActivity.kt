package com.widgetg7.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.widgetg7.mobile.R

class DexcomEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dexcom_entry)

        findViewById<MaterialButton>(R.id.openDexcomLoginButton).setOnClickListener {
            startActivity(
                Intent(this, DexcomSettingsActivity::class.java).apply {
                    putExtra(DexcomSettingsActivity.EXTRA_FIRST_CONNECTION_FLOW, true)
                }
            )
        }
    }
}
