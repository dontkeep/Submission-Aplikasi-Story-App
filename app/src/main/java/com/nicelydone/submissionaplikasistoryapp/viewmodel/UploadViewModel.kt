package com.nicelydone.submissionaplikasistoryapp.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.UploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val apiServices: ApiServices, sharedPreferences: SharedPreferences): ViewModel() {

   private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
   val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
   private val token = sharedPreferences.getString("token", "")

   fun uploadImage(
      description: String,
      imageUri: Uri?,
      latitude: Double?,
      longitude: Double?,
      context: Context
   ) {
      viewModelScope.launch {
         _uploadState.value = UploadState.Loading
         if (imageUri == null) {
            _uploadState.value = UploadState.Error("Please select an image")
            return@launch
         }
         try {
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = prepareFilePart("photo", imageUri, context)

            val latPart = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val lonPart = longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiServices.userUpload(
               "Bearer $token",
               descriptionPart,
               imagePart,
               latPart,
               lonPart
            )

            if (response.error == true) {
               _uploadState.value = UploadState.Error(response.message ?: "Upload failed")
            } else {
               _uploadState.value = UploadState.Success(response)
            }
         } catch (e: Exception) {
            _uploadState.value = UploadState.Error(e.message ?: "Unknown error")
         }
      }
   }

   private fun prepareFilePart(partName: String, fileUri: Uri, context: Context): MultipartBody.Part {
      val file = getFileFromUri(fileUri, context)
      val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
      return MultipartBody.Part.createFormData(partName, file.name, requestFile)
   }

   private fun getFileFromUri(uri: Uri, context: Context): File {
      val contentResolver: ContentResolver = context.contentResolver
      val inputStream = contentResolver.openInputStream(uri)
      val tempFile = File(context.cacheDir, System.currentTimeMillis().toString() + ".jpg")
      inputStream?.use { input ->
         FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
         }
      }
      return tempFile
   }
}

sealed class UploadState {
   object Idle : UploadState()
   object Loading : UploadState()
   data class Success(val response: UploadResponse) : UploadState()
   data class Error(val message: String) : UploadState()
}