package com.nicelydone.submissionaplikasistoryapp.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val apiServices: ApiServices, private val sharedPreferences: SharedPreferences): ViewModel() {
   private val _storyDetail = MutableLiveData<Story>()
   val storyDetail: LiveData<Story> = _storyDetail

   fun getStoryDetail(id: String) {
      val token = sharedPreferences.getString("token", null) ?: ""
      viewModelScope.launch {
         try {
            val response = apiServices.getDetailStory("Bearer $token", id)
            _storyDetail.value = response.story
         } catch (e: Exception) {
            Log.d("StoryDetailViewModel", "Error fetching stories: ${e.message}")
         } finally {
         }
      }
   }
}