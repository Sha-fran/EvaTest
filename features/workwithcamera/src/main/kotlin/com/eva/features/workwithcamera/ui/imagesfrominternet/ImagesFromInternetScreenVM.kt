package com.eva.features.workwithcamera.ui.imagesfrominternet

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eva.core.utils.saveimagetogallery.SaveImageToGallery
import com.eva.features.workwithcamera.domain.GetListOfImagesUseCase
import com.eva.features.workwithcamera.domain.entities.ListOfImagesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesFromInternetScreenVM @Inject constructor(
    private val getListOfImagesUseCase: GetListOfImagesUseCase,
    val saveImageToGallery: SaveImageToGallery
) : ViewModel() {
    private val _listOfImagesState =
        MutableStateFlow(SingleAppScreenUiState.empty)
    var listOfImagesState = _listOfImagesState.asStateFlow()

    init {
        getListOfImages()
    }

    private fun getListOfImages() {
        viewModelScope.launch {
            _listOfImagesState.update {
                it.copy(
                    listOfImages = getListOfImagesUseCase.getListOfImages()
                )
            }
        }
    }

    fun saveToGallery(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            saveImageToGallery(context, uri)
        }
    }

    data class SingleAppScreenUiState(
        val listOfImages: List<ListOfImagesEntity>
    ) {
        companion object {
            val empty = SingleAppScreenUiState(
                listOfImages = listOf()
            )
        }
    }
}