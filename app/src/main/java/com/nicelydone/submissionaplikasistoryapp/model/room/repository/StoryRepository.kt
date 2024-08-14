package com.nicelydone.submissionaplikasistoryapp.model.room.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nicelydone.submissionaplikasistoryapp.model.StoryRemoteMediator
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.room.ItemDatabase
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
   private val itemDatabase: ItemDatabase,
   private val apiService: ApiServices,
) {

   @OptIn(ExperimentalPagingApi::class)
   fun getStory(token: String): Flow<PagingData<StoryEntity>> {
      val pagingSourceFactory = { itemDatabase.storyDao().getStories() }
      return Pager(
         config = PagingConfig(
            pageSize = 5
         ),
         remoteMediator = StoryRemoteMediator(itemDatabase, apiService, token),
         pagingSourceFactory = pagingSourceFactory
      ).flow
   }
}