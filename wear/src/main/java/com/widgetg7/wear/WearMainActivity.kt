package com.widgetg7.wear

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

/** Écran minimal pour que l’app apparaisse dans le tiroir montre (découverte des tuiles / complications). */
class WearMainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv =
            TextView(this).apply {
                text = getString(R.string.wear_open_on_phone_hint)
                gravity = Gravity.CENTER
                setPadding(32, 32, 32, 32)
            }
        setContentView(tv)
    }
}
