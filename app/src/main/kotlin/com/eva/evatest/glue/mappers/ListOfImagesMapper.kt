package com.eva.evatest.glue.mappers

import com.eva.features.workwithcamera.domain.entities.EntityData
import com.eva.features.workwithcamera.domain.entities.EntityImages
import com.eva.features.workwithcamera.domain.entities.EntityOriginal
import com.eva.features.workwithcamera.domain.entities.ListOfImagesEntity
import com.eva.network.models.ListOfImagesModel

fun ListOfImagesModel.toEntity(): ListOfImagesEntity =
    ListOfImagesEntity(
        list = this.data.map { modelData ->
            EntityData(
                images = EntityImages(
                    original = EntityOriginal(
                        url = modelData.images.original.url
                    )
                )
            )
        }
    )