package com.eva.core.utils.saveimagetogallery

import android.content.Context
import android.net.Uri

fun interface SaveImageToGallery {
    suspend operator fun invoke(context: Context, uri: Uri)
}