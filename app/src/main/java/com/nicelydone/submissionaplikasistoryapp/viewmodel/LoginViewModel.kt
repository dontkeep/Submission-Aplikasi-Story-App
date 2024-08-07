package com.nicelydone.submissionaplikasistoryapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val storyApiServices: ApiServices, @ApplicationContext private val appContext: Context) : ViewModel() {
   private val _isLoading = MutableLiveData<Boolean>()
   val isLoading: LiveData<Boolean> = _isLoading

   private val _loginResult = MutableLiveData<Result<LoginResponse>>()
   val loginResult: LiveData<Result<LoginResponse>> = _loginResult

   fun login(email: String, password: String){
      viewModelScope.launch {
         _isLoading.value = true
         try {
            val response = storyApiServices.userLogin(email, password)
            _loginResult.value = Result.success(response)
            _isLoading.value = false
            saveSession(response.loginResult?.token)
         } catch (e: Exception) {
            _loginResult.value = Result.failure(e)
         } finally {
            _isLoading.value = false
         }
      }
   }

   private fun saveSession(token: String?) {
      val sharedPreferences = EncryptedSharedPreferences.create(
         "session_preferences",
         MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
         appContext,
         EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
         EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      )
      val editor = sharedPreferences.edit()
      editor.putString("token", token)
      editor.putBoolean("IS_LOGGED_IN", true)
      editor.apply()
   }
}