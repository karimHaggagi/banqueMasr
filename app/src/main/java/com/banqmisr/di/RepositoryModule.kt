package com.banqmisr.di

import com.banqmisr.data.repository.DetailsRepositoryImpl
import com.banqmisr.data.repository.HomeRepositoryImpl
import com.banqmisr.domain.repository.DetailsRepository
import com.banqmisr.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository =
        homeRepositoryImpl


    @Provides
    @Singleton
    fun provideDetailsRepository(detailsRepositoryImpl: DetailsRepositoryImpl): DetailsRepository =
        detailsRepositoryImpl
}