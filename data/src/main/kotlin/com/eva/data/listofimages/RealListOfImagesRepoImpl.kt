package com.eva.data.listofimages

import com.eva.data.RealListOfImagesRepo
import com.eva.network.EvaApiClient
import com.eva.network.EvaApiInterface
import com.eva.network.models.ListOfImagesModel
import javax.inject.Inject

class RealListOfImagesRepoImpl @Inject constructor(
    remote: EvaApiClient
): RealListOfImagesRepo {
    private val evaApiInterface = remote.evaApiClient.create(EvaApiInterface::class.java)

    override suspend fun getListOfImages(): List<ListOfImagesModel> =
        evaApiInterface.getListOfOrganisations()
}