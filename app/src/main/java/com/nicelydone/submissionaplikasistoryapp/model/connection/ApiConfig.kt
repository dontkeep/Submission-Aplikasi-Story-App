package com.nicelydone.submissionaplikasistoryapp.model.connection

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig(private val token: String) {
   private val BASE_URL = "https://story-api.dicoding.dev/v1/"
   fun getApiService(): ApiServices {
      val loggingInterceptor = HttpLoggingInterceptor()
         .setLevel(HttpLoggingInterceptor.Level.BODY)

      val client = OkHttpClient.Builder()
         .addInterceptor(loggingInterceptor)
         .build()

      val retrofit = Retrofit.Builder()
         .baseUrl(BASE_URL)
         .client(client)
         .addConverterFactory(GsonConverterFactory.create())
         .build()

      return retrofit.create(ApiServices::class.java)
   }
}