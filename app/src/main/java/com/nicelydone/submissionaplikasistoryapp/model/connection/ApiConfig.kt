package com.nicelydone.submissionaplikasistoryapp.model.connection

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.nicelydone.submissionaplikasistoryapp.BuildConfig
import okhttp3.Interceptor

class ApiConfig(private val token: String) {
   private val BASE_URL = BuildConfig.BASE_URL
   fun getApiService(): ApiServices {
      val loggingInterceptor = if (BuildConfig.DEBUG) {
         HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
      } else {
         HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
      }

      val authInterceptor = Interceptor { chain ->
         val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
         chain.proceed(request)
      }

      val client = OkHttpClient.Builder()
         .addInterceptor(loggingInterceptor)
         .addInterceptor(authInterceptor)
         .build()

      val retrofit = Retrofit.Builder()
         .baseUrl(BASE_URL)
         .client(client)
         .addConverterFactory(GsonConverterFactory.create())
         .build()

      return retrofit.create(ApiServices::class.java)
   }
}