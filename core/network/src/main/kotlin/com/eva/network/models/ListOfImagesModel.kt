package com.eva.network.models

import com.google.gson.annotations.SerializedName

data class ListOfImagesModel(
    @SerializedName("id") val id: String,
    @SerializedName("urls") val urls: Urls
)

data class Urls(
    @SerializedName("regular") val regular: String
)
