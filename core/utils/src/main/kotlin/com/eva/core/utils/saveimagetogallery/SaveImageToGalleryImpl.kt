package com.eva.core.utils.saveimagetogallery

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import com.eva.core.utils.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

const val SIMPLE_DATE_FORMAT = "yyyyMMdd_HHmmss"

@Singleton
class SaveImageToGalleryImpl @Inject constructor() : SaveImageToGallery {
    override suspend fun invoke(context: Context, uri: Uri) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = if (uri.scheme == context.getString(R.string.http) || uri.scheme == context.getString(R.string.https)) {
                val bitmap = downloadImage(context, uri)
                val tempFile = createTempFile(context)
                withContext(Dispatchers.IO) {
                    FileOutputStream(tempFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                }
                val tempUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    tempFile
                )
                contentResolver.openInputStream(tempUri)
                    ?: throw IllegalStateException("Could not open input stream for temp URI: $tempUri")
            } else {
                // Handle local URI: Use it directly
                contentResolver.openInputStream(uri)
                    ?: throw IllegalStateException("Could not open input stream for URI: $uri")
            }

            val timestamp = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault()).format(Date())
            val filename = "IMG_$timestamp.jpg"

            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val newImageDetails = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, context.getString(R.string.image_jpeg))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
            }

            val newImageUri = contentResolver.insert(imageCollection, newImageDetails)
                ?: throw IllegalStateException(context.getString(R.string.failed_to_create_new_image_uri))

            contentResolver.openOutputStream(newImageUri).use { outputStream ->
                inputStream.copyTo(outputStream!!)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                newImageDetails.clear()
                newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(newImageUri, newImageDetails, null, null)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.image_saved_to_gallery), Toast.LENGTH_SHORT).show()
            }

            withContext(Dispatchers.IO) {
                inputStream.close()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun downloadImage(context: Context, uri: Uri): Bitmap {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()
        val result = context.imageLoader.execute(request).drawable
        return (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
            ?: throw IllegalStateException(context.getString(R.string.failed_to_download_image_as_bitmap))
    }

    private fun createTempFile(context: Context): File {
        val timeStamp = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault()).format(Date())
        val imageFileName = "UNSPLASH_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
}