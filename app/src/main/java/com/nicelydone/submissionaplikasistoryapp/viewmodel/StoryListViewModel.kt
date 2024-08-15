package com.nicelydone.submissionaplikasistoryapp.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.ListStoryItem
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity
import com.nicelydone.submissionaplikasistoryapp.model.room.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
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
   private val _stories = MutableLiveData<PagingData<StoryEntity>>()
   val stories: LiveData<PagingData<StoryEntity>> get() = _stories

   private val _storyWithLocation = MutableLiveData<List<ListStoryItem?>?>()
   val storyWithLocation: LiveData<List<ListStoryItem?>?> get() = _storyWithLocation


   init {
      refreshStories()
      fetchStoriesWithLocation()
   }

   fun refreshStories() {
      viewModelScope.launch {
         storyRepository.getStory(token).cachedIn(viewModelScope).collectLatest { pagingData ->
            _stories.postValue(pagingData)
         }
      }
   }

   fun fetchStoriesWithLocation() {
      viewModelScope.launch {
         try {
            val response = apiServices.getStoriesWithLocation("Bearer $token")
            if (response.isSuccessful) {
               _storyWithLocation.postValue(response.body()?.listStory)
            } else {
               _storyWithLocation.postValue(null)
            }
         } catch (e: Exception) {
            _storyWithLocation.postValue(null)
         }
      }
   }
}