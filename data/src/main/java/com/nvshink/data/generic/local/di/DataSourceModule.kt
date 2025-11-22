package com.nvshink.data.generic.local.di

import com.nvshink.data.generic.local.datasource.DataSourceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideDataSourceManager(): DataSourceManager = DataSourceManager()
}
