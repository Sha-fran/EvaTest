package com.eva.features.workwithcamera.ui.workwithcamera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eva.core.utils.filters.ImageFilter
import com.eva.core.utils.photo.applyFilterToImage
import com.eva.core.utils.saveimagetogallery.SaveImageToGallery
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkWithCameraScreenVM @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveImageToGallery: SaveImageToGallery
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