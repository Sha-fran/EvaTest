package com.eva.navigation

import androidx.annotation.StringRes

sealed class Route(@StringRes val titleRes: Int = 0) {
    data object WorkWithCameraScreen : Route(R.string.get_image)
    data object ImagesFromInternet : Route(R.string.get_image)
}
