package com.eva.network.models

import com.google.gson.annotations.SerializedName

data class ListOfImagesModel(
    @SerializedName("data") var data: List<Data>
)

data class Data(
    @SerializedName("images") var images:Images
)

data class Images(
    @SerializedName("original") var original:Original
)

data class Original(
    @SerializedName("url") var url:String
)
