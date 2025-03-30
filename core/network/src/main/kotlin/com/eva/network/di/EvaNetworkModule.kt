package com.eva.network.di

import com.eva.network.EvaApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EvaNetworkModule {
    @Provides
    @Singleton
    fun getEvaApiClient() = EvaApiClient()
}