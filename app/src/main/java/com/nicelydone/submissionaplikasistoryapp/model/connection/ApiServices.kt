package com.nicelydone.submissionaplikasistoryapp.model.connection

import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.LoginResponse
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.RegistResponse
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.StoryResponse
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiServices {
   @FormUrlEncoded
   @POST("login")
   suspend fun userLogin(
      @Field("email") email: String,
      @Field("password") password: String
   ): LoginResponse

   @FormUrlEncoded
   @POST("register")
   suspend fun userRegist(
      @Field("name") name: String,
      @Field("email") email: String,
      @Field("password") password: String
   ): RegistResponse

   @Multipart
   @POST("stories")
   suspend fun userUpload(
      @Header("Authorization") token: String,
      @Part("description") description: RequestBody,
      @Part file: MultipartBody.Part,
      @Part("lat") lat: RequestBody?,
      @Part("lon") lon: RequestBody?
   ): UploadResponse

   @GET("stories")
   suspend fun getAllStories(
      @Header("Authorization") token: String,
      @Query("page") page: Int,
      @Query("size") size: Int
   ): StoryResponse

   @GET("stories")
   suspend fun getStoriesWithLocation(
      @Header("Authorization") token: String,
      @Query("location") location: Int = 1,
      @Query("size") size: Int = 30
   ): Response<StoryResponse>
}