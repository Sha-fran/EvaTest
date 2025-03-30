package com.eva.data.listofimages.di

import com.eva.data.RealListOfImagesRepo
import com.eva.data.listofimages.RealListOfImagesRepoImpl
import com.eva.network.EvaApiClient
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object EvaTestDataModule {

    fun provideListOfImagesRepo(remote: EvaApiClient): RealListOfImagesRepo =
        RealListOfImagesRepoImpl(remote)
}