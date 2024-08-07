package com.nicelydone.submissionaplikasistoryapp.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.ListStoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryListViewModel @Inject constructor(private val apiServices: ApiServices, private val sharedPreferences: SharedPreferences) : ViewModel() {
   private val _stories = MutableLiveData<List<ListStoryItem>>()
   val stories: LiveData<List<ListStoryItem>> = _stories

   private val _isLoading = MutableLiveData<Boolean>()
   val isLoading: LiveData<Boolean> = _isLoading

   fun getStories() {
      _isLoading.value = true
      val token = sharedPreferences.getString("token", null) ?: ""
      viewModelScope.launch {
         try {
            val response = apiServices.getAllStories("Bearer $token")
            _stories.value = response.listStory?.filterNotNull() ?: emptyList()
         } catch (e: Exception) {
            Log.d("StoryListViewModel", "Error fetching stories: ${e.message}")
         } finally {
            _isLoading.value = false
         }
      }
   }
}