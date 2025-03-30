package com.eva.core.utils.photo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import com.eva.core.utils.R
import com.eva.core.utils.filters.ImageFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val SIMPLE_DATE_FORMAT = "yyyyMMdd_HHmmss"

fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = createTempImageFile(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                onImageCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}

suspend fun applyFilterToImage(context: Context, uri: Uri, filter: ImageFilter): Uri {

    if (filter == ImageFilter.NONE) {
        return uri
    }

    val request = ImageRequest.Builder(context)
        .data(uri)
        .transformations(filter.getTransformation())
        .build()
    val result = context.imageLoader.execute(request).drawable


    val bitmap = (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
        ?: throw IllegalStateException(context.getString(R.string.error_failed_to_convert_bitmap))

    val timeStamp = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault()).format(Date())
    val imageFileName = "FILTERED_${timeStamp}_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val filteredFile = withContext(Dispatchers.IO) {
        File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    withContext(Dispatchers.IO) {
        FileOutputStream(filteredFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        filteredFile
    )
}