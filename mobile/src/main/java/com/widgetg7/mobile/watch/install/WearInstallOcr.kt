package com.widgetg7.mobile.watch.install

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import kotlin.math.max
import kotlin.math.roundToInt

object WearInstallOcr {
    const val DECODE_FAILED_MESSAGE = "wg7_wear_ocr_decode"

    private const val MAX_BITMAP_SIDE = 2048

    suspend fun recognizeText(context: Context, uri: Uri): String {
        val decoded = decodeUriToBitmap(context, uri) ?: throw IllegalStateException(DECODE_FAILED_MESSAGE)
        val (bitmap, rotationDegrees) = decoded
        val image = InputImage.fromBitmap(bitmap, rotationDegrees)
        val client = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return try {
            client.process(image).await().text.orEmpty()
        } finally {
            client.close()
            bitmap.recycle()
        }
    }

    private fun decodeUriToBitmap(context: Context, uri: Uri): Pair<Bitmap, Int>? {
        return try {
            val rotation = exifRotationDegrees(context, uri)

            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, bounds)
            } ?: return null

            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

            val inSampleSize = computeInSampleSize(bounds, MAX_BITMAP_SIDE)
            val opts =
                BitmapFactory.Options().apply {
                    this.inSampleSize = inSampleSize
                }
            val bmp =
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, opts)
                } ?: return null

            val scaled = scaleDownIfNeeded(bmp)
            if (scaled != bmp) bmp.recycle()

            Pair(scaled, rotation)
        } catch (_: Exception) {
            null
        }
    }

    private fun exifRotationDegrees(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                when (
                    exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL,
                    )
                ) {
                    ExifInterface.ORIENTATION_ROTATE_90,
                    ExifInterface.ORIENTATION_TRANSPOSE,
                    -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270,
                    ExifInterface.ORIENTATION_TRANSVERSE,
                    -> 270
                    else -> 0
                }
            } ?: 0
        } catch (_: Exception) {
            0
        }
    }

    private fun computeInSampleSize(opts: BitmapFactory.Options, maxSide: Int): Int {
        var inSampleSize = 1
        val longest = max(opts.outHeight, opts.outWidth).coerceAtLeast(1)
        while (longest / inSampleSize > maxSide) {
            inSampleSize *= 2
        }
        return max(1, inSampleSize)
    }

    private fun scaleDownIfNeeded(src: Bitmap): Bitmap {
        val w = src.width
        val h = src.height
        val maxSide = max(w, h)
        if (maxSide <= MAX_BITMAP_SIDE) return src
        val ratio = MAX_BITMAP_SIDE.toFloat() / maxSide
        val tw = max(1, (w * ratio).roundToInt())
        val th = max(1, (h * ratio).roundToInt())
        return Bitmap.createScaledBitmap(src, tw, th, true)
    }
}
