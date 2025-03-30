package com.eva.features.workwithcamera.domain.entities

data class ListOfImagesEntity(
    val list:List<EntityData>
)

data class EntityData(
    var images:EntityImages
)

data class EntityImages(
    var original:EntityOriginal
)

data class EntityOriginal(
    var url:String
)