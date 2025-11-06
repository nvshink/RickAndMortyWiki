package com.nvshink.data.generic.local.di

import android.content.Context
import androidx.room.Room
import androidx.room.TypeConverters
import com.nvshink.data.character.local.converter.CharacterTypeConverter
import com.nvshink.data.character.local.dao.CharacterDao
import com.nvshink.data.episode.local.dao.EpisodeDao
import com.nvshink.data.generic.local.room.RickAndMortyWikiDB
import com.nvshink.data.location.local.dao.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@TypeConverters(CharacterTypeConverter::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext context: Context): RickAndMortyWikiDB {
        return Room.databaseBuilder(
            context,
            RickAndMortyWikiDB::class.java,
            "local_db"
        ).build()
    }
    @Provides
    @Singleton
    fun provideExerciseDao(db: RickAndMortyWikiDB): CharacterDao = db.characterDao

    @Provides
    @Singleton
    fun provideTrainingPlanDao(db: RickAndMortyWikiDB): LocationDao = db.locationDao

    @Provides
    @Singleton
    fun provideTrainingPlanExerciseDao(db: RickAndMortyWikiDB): EpisodeDao = db.episodeDao


}