package com.widgetg7.mobile.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.widgetg7.core.model.AgpGlucoseColors
import com.widgetg7.mobile.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/**
 * Renders the real home layout ([R.layout.activity_main]) to PNG — actual app XML/theme,
 * without emulator or device.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppPreviewExporterTest {
    @Test
    fun exportMobileHomePreview() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val themed = ContextThemeWrapper(appContext, R.style.Theme_WidgetG7_Phone)
        val root =
            LayoutInflater.from(themed).inflate(R.layout.activity_main, null, false)
        bindPreviewState(themed, root)

        val bitmap = captureView(root)
        val outDir = File("build/app-previews").also { it.mkdirs() }
        val outFile = File(outDir, "mobile-home.png")
        FileOutputStream(outFile).use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) { "PNG encode failed" }
        }
        println("APP_PREVIEW=${outFile.absolutePath}")
    }

    private fun bindPreviewState(context: Context, root: View) {
        root.findViewById<TextView>(R.id.homeReadingPrimary).apply {
            text = "120"
            setTextColor(AgpGlucoseColors.IN_RANGE)
        }
        root.findViewById<TextView>(R.id.homeReadingSubtitle).apply {
            text = "mg/dL ↗ · il y a 2 min"
            visibility = View.VISIBLE
        }
        root.findViewById<TextView>(R.id.homeStatusText).apply {
            text = context.getString(R.string.home_status_sync_active)
            setBackgroundResource(R.drawable.bg_status_pill_ok)
            setTextColor(AgpGlucoseColors.IN_RANGE)
        }
        root.findViewById<TextView>(R.id.dexcomCardStatus).text =
            context.getString(R.string.home_dexcom_status_on)
        root.findViewById<View>(R.id.dexcomStatusButton)
            .setBackgroundResource(R.drawable.bg_dexcom_status_connected)
        root.findViewById<View>(R.id.dexcomStatusDot)
            .setBackgroundResource(R.drawable.bg_status_dot_connected)
        root.findViewById<TextView>(R.id.watchCardStatus).text =
            context.getString(R.string.home_watch_status_ok, "Pixel Watch 2")
        root.findViewById<ImageView>(R.id.watchCardImage)
            .setImageResource(R.drawable.watch_reference_hero)
        root.findViewById<View>(R.id.watchStatusDot)
            .setBackgroundResource(R.drawable.bg_status_dot_connected)
    }

    private fun captureView(view: View): Bitmap {
        val widthPx = 1080
        val heightPx = 2400
        view.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.AT_MOST),
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap =
            Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888,
            )
        view.draw(Canvas(bitmap))
        return bitmap
    }
}
