package com.nicelydone.submissionaplikasistoryapp.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.ListStoryItem
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity
import com.nicelydone.submissionaplikasistoryapp.model.room.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryListViewModel @Inject constructor(
   private val storyRepository: StoryRepository,
   private val apiServices: ApiServices,
   private val sharedPreferences: SharedPreferences
) : ViewModel() {

   private val token: String
      get() = sharedPreferences.getString("token", "") ?: ""

   val story: LiveData<PagingData<StoryEntity>> = storyRepository.getStory(token).cachedIn(viewModelScope).asLiveData()

   fun fetchStoriesWithLocation(): LiveData<List<ListStoryItem>?> {
      val liveData = MutableLiveData<List<ListStoryItem>?>()
      viewModelScope.launch {
         try {
            val response = apiServices.getStoriesWithLocation("Bearer " + token)
            if (response.isSuccessful) {
               liveData.postValue(response.body()?.listStory as List<ListStoryItem>?)
            } else {
               Log.e("StoryListViewModel", "Error fetching stories with location: ${response.code()}")
            }
         } catch (e: Exception) {
            Log.e("StoryListViewModel", "Error fetching stories with location", e)
         }
      }
      return liveData
   }
}