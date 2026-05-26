package com.widgetg7.mobile.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.widgetg7.mobile.R

/** Displays the in-app user notice from raw assets. */
class NoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.noticeContentText).text =
            resources.openRawResource(R.raw.notice_utilisateur)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
    }
}
