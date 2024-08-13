package com.nicelydone.submissionaplikasistoryapp.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainActivityModule {

   @Qualifier
      @Retention(AnnotationRetention.BINARY)
      annotation class ZeroArgConstructor

   @Provides
   @Singleton
   fun provideSharedPreferences(
      @ApplicationContext context: Context
   ): SharedPreferences {
      return EncryptedSharedPreferences.create(
         "session_preferences",
         MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
         context,
         EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
         EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      )
   }

   @Provides
   @ZeroArgConstructor
   fun provideSharedPreferencesForZeroArgConstructor(@ApplicationContext context: Context):
       SharedPreferences {
      return provideSharedPreferences(context)
   }
}