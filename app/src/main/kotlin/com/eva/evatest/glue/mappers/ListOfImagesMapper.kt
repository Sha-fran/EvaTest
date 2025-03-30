package com.eva.evatest.glue.mappers

import com.eva.features.workwithcamera.domain.entities.ListOfImagesEntity
import com.eva.network.models.ListOfImagesModel

fun ListOfImagesModel.toEntity(): ListOfImagesEntity {
    return ListOfImagesEntity(url = this.urls.regular)
}