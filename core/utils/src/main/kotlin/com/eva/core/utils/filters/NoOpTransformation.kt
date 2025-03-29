package com.eva.core.utils.filters

import android.graphics.Bitmap
import coil.transform.Transformation

class NoOpTransformation : Transformation {
    override val cacheKey: String = "no_op"
    override suspend fun transform(input: Bitmap, size: coil.size.Size): Bitmap = input
}