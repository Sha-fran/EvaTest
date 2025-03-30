package com.eva.core.utils.filters

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import coil.transform.Transformation

class BrightnessTransformation : Transformation {
    override val cacheKey: String = "brightness"
    override suspend fun transform(input: Bitmap, size: coil.size.Size): Bitmap {
        val output = Bitmap.createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val matrix = ColorMatrix().apply {
            setScale(1.2f, 1.2f, 1.2f, 1f) // Increase brightness by scaling RGB
        }
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(input, 0f, 0f, paint)
        return output
    }
}