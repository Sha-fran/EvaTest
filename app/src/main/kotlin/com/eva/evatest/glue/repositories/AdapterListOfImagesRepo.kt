package com.eva.evatest.glue.repositories

import com.eva.data.RealListOfImagesRepo
import com.eva.evatest.glue.mappers.toEntity
import com.eva.features.workwithcamera.domain.entities.ListOfImagesEntity
import com.eva.features.workwithcamera.domain.repositories.ListOfImagesRepo
import javax.inject.Inject

class AdapterListOfImagesRepo @Inject constructor(
    private val realListOfImagesRepo: RealListOfImagesRepo

): ListOfImagesRepo {
    override suspend fun getListOfImages(): List<ListOfImagesEntity> =
        realListOfImagesRepo.getListOfImages().map { it.toEntity() }
}