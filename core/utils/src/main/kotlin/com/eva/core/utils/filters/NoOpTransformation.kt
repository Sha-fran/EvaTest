package com.eva.core.utils.filters

import android.graphics.Bitmap
import coil.transform.Transformation

const val CACHE_KEY_NO_OP = "no_op"

class NoOpTransformation : Transformation {
    override val cacheKey: String = CACHE_KEY_NO_OP
    override suspend fun transform(input: Bitmap, size: coil.size.Size): Bitmap = input
}