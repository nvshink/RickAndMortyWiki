package com.nvshink.data.generic.di

import com.nvshink.data.character.repository.CharacterRepositoryImpl
import com.nvshink.data.episode.repository.EpisodeRepositoryImpl
import com.nvshink.data.location.repository.LocationRepositoryImpl
import com.nvshink.domain.character.repository.CharacterRepository
import com.nvshink.domain.episode.repository.EpisodeRepository
import com.nvshink.domain.location.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindCharacterRepository(characterRepositoryImpl: CharacterRepositoryImpl): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindEpisodeRepository(episodeRepositoryImpl: EpisodeRepositoryImpl): EpisodeRepository
}