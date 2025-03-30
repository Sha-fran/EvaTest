package com.eva.data

import com.eva.network.models.ListOfImagesModel

interface RealListOfImagesRepo {
    suspend fun getListOfImages():List<ListOfImagesModel>
}