package com.eva.core.utils.saveimagetogallery

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SaveImageToGalleryModule {
    @Binds
    @Singleton
    fun bindSaveImageToGallery(impl: SaveImageToGalleryImpl): SaveImageToGallery
}