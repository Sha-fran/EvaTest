package com.eva.network

import com.eva.network.models.ListOfImagesModel
import retrofit2.http.GET
import retrofit2.http.Headers

interface EvaApiInterface {
    @Headers("Authorization: Client-ID 0tFeX8qN4UXcgWJKDJOQFwSGjuC8rmOfOZYCEFgc-v8")
    @GET("/photos/random?count=20")
    suspend fun getListOfOrganisations(): List<ListOfImagesModel>
}