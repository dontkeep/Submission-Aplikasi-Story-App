package com.nicelydone.submissionaplikasistoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.submissionaplikasistoryapp.model.room.dao.StoryDao
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val storyDao: StoryDao): ViewModel() {
   private val _storyDetail = MutableLiveData<StoryEntity?>()
   val storyDetail: LiveData<StoryEntity?> = _storyDetail

   fun getStoryDetail(id: String) {
      viewModelScope.launch {
         val story = storyDao.getStoryById(id)
         _storyDetail.value = story
      }
   }
}