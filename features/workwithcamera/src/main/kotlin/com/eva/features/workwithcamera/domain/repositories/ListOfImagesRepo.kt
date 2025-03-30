package com.eva.features.workwithcamera.domain.repositories

import com.eva.features.workwithcamera.domain.entities.ListOfImagesEntity

interface ListOfImagesRepo {
    suspend fun getListOfImages(): List<ListOfImagesEntity>
}