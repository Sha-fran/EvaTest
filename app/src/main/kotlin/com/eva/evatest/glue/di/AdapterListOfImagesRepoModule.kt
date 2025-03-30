package com.eva.evatest.glue.di

import com.eva.evatest.glue.repositories.AdapterListOfImagesRepo
import com.eva.features.workwithcamera.domain.repositories.ListOfImagesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AdapterListOfImagesRepoModule {

    @Binds
    fun bindListOfImagesRepo(
        listOfImagesRepo: AdapterListOfImagesRepo
    ): ListOfImagesRepo
}