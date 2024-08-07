package com.nicelydone.submissionaplikasistoryapp.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.viewmodel.RegisterViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor (private val apiServices: ApiServices) : ViewModelProvider.Factory {
   override fun <T : ViewModel>create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
         @Suppress("UNCHECKED_CAST")
         return RegisterViewModel(apiServices) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}