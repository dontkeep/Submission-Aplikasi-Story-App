package com.nicelydone.submissionaplikasistoryapp.helper

import android.content.SharedPreferences
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiConfig
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object Injection {
   @Provides
   fun  provideApiConfig(sharedPreferences: SharedPreferences): ApiConfig {
      val token = sharedPreferences.getString("token", "") ?: ""
      return ApiConfig(token)
   }

   @Provides
   fun provideApiService(apiConfig: ApiConfig): ApiServices {
      return apiConfig.getApiService()
   }
}