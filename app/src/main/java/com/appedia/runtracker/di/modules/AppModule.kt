package com.appedia.runtracker.di.modules

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.appedia.runtracker.data.db.database.RunTrackerDatabase
import com.appedia.runtracker.util.Constants.KEY_IS_SETUP_DONE
import com.appedia.runtracker.util.Constants.KEY_NAME
import com.appedia.runtracker.util.Constants.KEY_WEIGHT
import com.appedia.runtracker.util.Constants.RUN_TRACKER_DATABASE_NAME
import com.appedia.runtracker.util.Constants.SHARED_PREFERENCES_NAME
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
    @Singleton
    fun providesRunDao(database: RunTrackerDatabase) = database.getRunDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME, "User") ?: "User"

    @Provides
    @Singleton
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Provides
    @Singleton
    fun provideIsSetupDone(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_IS_SETUP_DONE, false)

}