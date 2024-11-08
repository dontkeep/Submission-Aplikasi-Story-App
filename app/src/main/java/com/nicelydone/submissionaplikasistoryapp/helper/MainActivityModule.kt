package com.nicelydone.submissionaplikasistoryapp.helper

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainActivityModule {

   @Provides
   @Singleton
   fun provideSharedPreferences(
      application: Application
   ): SharedPreferences {
      return application.getSharedPreferences("session_preferences", Context.MODE_PRIVATE)
   }
}