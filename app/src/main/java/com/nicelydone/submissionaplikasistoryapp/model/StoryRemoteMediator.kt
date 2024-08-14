package com.nicelydone.submissionaplikasistoryapp.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.room.ItemDatabase
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.RemoteKeys
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val database: ItemDatabase, private val apiService: ApiServices, private val token: String) : RemoteMediator<Int, StoryEntity>() {

   private companion object {
      const val INITIAL_PAGE_INDEX = 1
   }


   override suspend fun load(
      loadType: LoadType,
      state: PagingState<Int, StoryEntity>
   ): MediatorResult {
      val page = when(loadType) {
         LoadType.REFRESH -> {
            val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
               remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
         }
         LoadType.PREPEND -> {
            val remoteKeys = getRemoteKeyForFirstItem(state)
            val prevKey = remoteKeys?.prevKey
               ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            prevKey
         }
         LoadType.APPEND -> {
            val remoteKeys = getRemoteKeyForLastItem(state)
            val nextKey = remoteKeys?.nextKey
               ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            nextKey
         }
      }
      try {
         val responseData = apiService.getAllStories("Bearer "+ token, page, state.config.pageSize)
         val stories = responseData.listStory?.map {
            StoryEntity(
               id = it?.id ?: "",
               photoUrl = it?.photoUrl ?: "",
               createdAt = it?.createdAt ?: "",
               name = it?.name ?: "",description = it?.description ?: "",
               lon = it?.lon ?: 0.0,
               lat = it?.lat ?: 0.0
            )
         } ?: emptyList()

         val endOfPaginationReached = stories.isEmpty()
         database.withTransaction {
            if (loadType == LoadType.REFRESH) {
               database.remoteKeysDao().clearRemoteKeys()
               database.storyDao().clearStories()
            }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = stories.map {
               RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            database.remoteKeysDao().insertAllRemoteKeys(keys)
            database.storyDao().insertAll(stories)
         }
         return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
      } catch (exception: Exception) {
         return MediatorResult.Error(exception)
      }
   }

   private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
      return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
         database.remoteKeysDao().remoteKeysStoryId(data.id)
      }
   }

   private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
      return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
         database.remoteKeysDao().remoteKeysStoryId(data.id)
      }
   }

   private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
      return state.anchorPosition?.let { position ->
         state.closestItemToPosition(position)?.id?.let { repoId ->
            database.remoteKeysDao().remoteKeysStoryId(repoId)
         }
      }
   }
}