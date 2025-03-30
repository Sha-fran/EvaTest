package com.eva.features.workwithcamera.domain

import com.eva.features.workwithcamera.domain.entities.ListOfImagesEntity
import com.eva.features.workwithcamera.domain.repositories.ListOfImagesRepo
import javax.inject.Inject

class GetListOfImagesUseCase @Inject constructor(
    private val listOfImagesRepo: ListOfImagesRepo
) {
    suspend fun getListOfImages(): List<ListOfImagesEntity> = listOfImagesRepo.getListOfImages()
}