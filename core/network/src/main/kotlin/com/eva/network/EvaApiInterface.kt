package com.eva.network

import com.eva.network.models.ListOfImagesModel
import retrofit2.http.GET

interface EvaApiInterface {
    @GET("/v2/emoji?api_key=80xeeh9hOqxgQPcSfXE5q4uNiA2QQqeO&limit=20&offset=0")
    suspend fun getListOfOrganisations(): List<ListOfImagesModel>
}