package com.nicelydone.submissionaplikasistoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.RegistResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val apiServices: ApiServices): ViewModel() {
   private val _isLoading = MutableLiveData<Boolean>()
   val isLoading: LiveData<Boolean> = _isLoading

   private val _registerResult = MutableLiveData<Result<RegistResponse>>()
   val registerResult: LiveData<Result<RegistResponse>> = _registerResult

   fun register(name: String, email: String, password: String) {
      viewModelScope.launch {
         _isLoading.value = true
         try {
            val response = apiServices.userRegist(name, email, password)
            _registerResult.value = Result.success(response)
         } catch (e: Exception) {
            _registerResult.value = Result.failure(e)
         } finally {
            _isLoading.value = false
         }
      }
   }
}