package com.eva.features.workwithcamera.ui.workwithcamera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eva.core.utils.filters.ImageFilter
import com.eva.core.utils.photo.applyFilterToImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WorkWithCameraScreenVM @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkWithCameraUiState.empty)
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.OpenCamera -> openCamera()
            is CameraEvent.OpenGallery -> openGallery()
            is CameraEvent.SwitchCamera -> switchCamera()
            is CameraEvent.TakePhoto -> takePhoto(event.imageCapture)
            is CameraEvent.ImageCaptured -> onImageCaptured(event.uri)
            is CameraEvent.SaveToGallery -> saveToGallery()
            is CameraEvent.PermissionGranted -> updatePermission(event.permission, true)
            is CameraEvent.PermissionDenied -> updatePermission(event.permission, false)
            is CameraEvent.ApplyFilter -> applyFilter(event.filter)
        }
    }

    private fun openCamera() {
        _uiState.update { it.copy(showCameraView = true) }
    }

    private fun openGallery() {
        _uiState.update { it.copy(showCameraView = false) }
    }

    private fun switchCamera() {
        _uiState.update {
            it.copy(
                lensFacing = if (it.lensFacing == CameraSelector.LENS_FACING_BACK) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
            )
        }
    }

    private fun takePhoto(imageCapture: ImageCapture) {
        viewModelScope.launch(Dispatchers.IO) {
            com.eva.core.utils.photo.takePhoto(
                imageCapture = imageCapture,
                context = context,
                onImageCaptured = { uri ->
                    onImageCaptured(uri)
                },
                onError = { exception ->
                    Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()

                }
            )
        }
    }

    private fun onImageCaptured(uri: Uri) {
        _uiState.update {
            it.copy(
                imageUri = uri,
                originalImageUri = uri,
                showCameraView = false,
                appliedFilter = ImageFilter.NONE
            )
        }
    }

    private fun saveToGallery() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.imageUri?.let { uri ->
                saveImageToGallery(context, uri)
            }
        }
    }

    private fun updatePermission(permission: String, isGranted: Boolean) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                _uiState.update { it.copy(hasCameraPermission = isGranted) }
            }

            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                _uiState.update { it.copy(hasGalleryPermission = isGranted) }
            }
        }
    }

    private fun applyFilter(filter: ImageFilter) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.imageUri?.let { currentUri ->
                if (filter == ImageFilter.NONE) {
                    _uiState.update {
                        it.copy(
                            imageUri = it.originalImageUri,
                            appliedFilter = ImageFilter.NONE
                        )
                    }
                } else {
                    val filteredUri = applyFilterToImage(context, currentUri, filter)
                    _uiState.update {
                        it.copy(
                            imageUri = filteredUri,
                            appliedFilter = filter
                        )
                    }
                }
            }
        }
    }
}

private suspend fun saveImageToGallery(context: Context, uri: Uri) {
    try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream != null) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "IMG_$timestamp.jpg"

            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val newImageDetails = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
            }

            val newImageUri = contentResolver.insert(imageCollection, newImageDetails)

            if (newImageUri != null) {
                contentResolver.openOutputStream(newImageUri).use { outputStream ->
                    inputStream.copyTo(outputStream!!)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    newImageDetails.clear()
                    newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(newImageUri, newImageDetails, null, null)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }

            withContext(Dispatchers.IO) {
                inputStream.close()
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

data class WorkWithCameraUiState(
    val imageUri: Uri? = null,
    val originalImageUri: Uri? = null,
    val hasCameraPermission: Boolean = false,
    val hasGalleryPermission: Boolean = false,
    val showCameraView: Boolean = false,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val appliedFilter: ImageFilter = ImageFilter.NONE
) {
    companion object {
        val empty = WorkWithCameraUiState()
    }
}

sealed class CameraEvent {
    data object OpenCamera : CameraEvent()
    data object OpenGallery : CameraEvent()
    data object SwitchCamera : CameraEvent()
    data class TakePhoto(val imageCapture: ImageCapture) : CameraEvent()
    data class ImageCaptured(val uri: Uri) : CameraEvent()
    data object SaveToGallery : CameraEvent()
    data class PermissionGranted(val permission: String) : CameraEvent()
    data class PermissionDenied(val permission: String) : CameraEvent()
    data class ApplyFilter(val filter: ImageFilter) : CameraEvent()
}