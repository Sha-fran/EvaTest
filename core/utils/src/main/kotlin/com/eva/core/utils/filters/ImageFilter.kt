package com.eva.core.utils.filters

import coil.transform.Transformation

enum class ImageFilter {
    NONE,
    GRAYSCALE,
    SEPIA,
    BRIGHTNESS;

    fun getTransformation(): Transformation = when (this) {
        NONE -> NoOpTransformation()
        GRAYSCALE -> GrayscaleTransformation()
        SEPIA -> SepiaTransformation()
        BRIGHTNESS -> BrightnessTransformation()
    }
}