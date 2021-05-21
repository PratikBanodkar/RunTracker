package com.appedia.runtracker.di.modules

import android.content.Context
import com.appedia.runtracker.util.RunTimer
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context) =
        FusedLocationProviderClient(context)

    @ServiceScoped
    @Provides
    fun providesRunTimer() = RunTimer()

}