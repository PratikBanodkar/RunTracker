package com.appedia.runtracker.di.modules

import android.content.Context
import androidx.room.Room
import com.appedia.runtracker.data.db.database.RunTrackerDatabase
import com.appedia.runtracker.util.Constants.RUN_TRACKER_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRunTrackerDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        RunTrackerDatabase::class.java,
        RUN_TRACKER_DATABASE_NAME
    ).build()

    @Provides
    fun providesRunDao(database: RunTrackerDatabase) = database.getRunDao()
}