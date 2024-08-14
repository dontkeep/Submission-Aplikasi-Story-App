package com.nicelydone.submissionaplikasistoryapp.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.nicelydone.submissionaplikasistoryapp.DataDummy
import com.nicelydone.submissionaplikasistoryapp.MainDispatcherRule
import com.nicelydone.submissionaplikasistoryapp.getOrAwaitValue
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity
import com.nicelydone.submissionaplikasistoryapp.model.room.repository.StoryRepository
import com.nicelydone.submissionaplikasistoryapp.ui.adapter.StoryListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryListViewModelTest {

   @get:Rule
   val instantExecutorRule = InstantTaskExecutorRule()

   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Mock
   private lateinit var storyRepository: StoryRepository

   @Mock
   private lateinit var sharedPreferences: SharedPreferences

   @Mock
   private lateinit var apiServices: ApiServices

   private val dummyToken = "Bearer token123"

   @Test
   fun `when Get Story Should Not Be Null and Return Data`() = runTest {
      val dummyStories = DataDummy.generateDummyStoryResponse()
      val data: PagingData<StoryEntity> = PagingData.from(dummyStories.map {
         StoryEntity(
            id = it.id ?: "",
            createdAt = it.createdAt ?: "",
            name = it.name ?: "",
            description = it.description ?: "",
            lon = it.lon ?: 0.0,
            photoUrl = it.photoUrl ?: "",
            lat = it.lat ?: 0.0
         )
      })

      Mockito.`when`(sharedPreferences.getString("token", "")).thenReturn(dummyToken)
      Mockito.`when`(storyRepository.getStory(dummyToken)).thenReturn(flowOf(data))

      val storyListViewModel = StoryListViewModel(storyRepository, apiServices, sharedPreferences)
      val actualPagingData = storyListViewModel.stories.getOrAwaitValue()

      val differ = AsyncPagingDataDiffer(
         diffCallback = StoryListAdapter.DIFF_CALLBACK,
         updateCallback = noopListUpdateCallback,
         workerDispatcher = Dispatchers.Main
      )
      differ.submitData(actualPagingData)

      assertNotNull(differ.snapshot())
      assertEquals(dummyStories.size, differ.snapshot().size)
      assertEquals(dummyStories.first().id, differ.snapshot().firstOrNull()?.id)
      assertEquals(dummyStories.first().name, differ.snapshot().firstOrNull()?.name)
      assertEquals(dummyStories.first().description, differ.snapshot().firstOrNull()?.description)
      assertEquals(dummyStories.first().photoUrl, differ.snapshot().firstOrNull()?.photoUrl)
      assertEquals(dummyStories.first().createdAt, differ.snapshot().firstOrNull()?.createdAt)
      assertEquals(dummyStories.first().lat, differ.snapshot().firstOrNull()?.lat)
      assertEquals(dummyStories.first().lon, differ.snapshot().firstOrNull()?.lon)
   }

   @Test
   fun `when Get Story is Empty Should Return No Data`() = runTest {
      val data: PagingData<StoryEntity> = PagingData.empty()

      Mockito.`when`(sharedPreferences.getString("token", "")).thenReturn(dummyToken)
      Mockito.`when`(storyRepository.getStory(dummyToken)).thenReturn(flowOf(data))

      val storyListViewModel = StoryListViewModel(storyRepository, apiServices, sharedPreferences)
      val actualPagingData = storyListViewModel.stories.getOrAwaitValue()

      val differ = AsyncPagingDataDiffer(
         diffCallback = StoryListAdapter.DIFF_CALLBACK,
         updateCallback = noopListUpdateCallback,
         workerDispatcher = Dispatchers.Main
      )
      differ.submitData(actualPagingData)

      assertEquals(0, differ.snapshot().size)
   }
}

val noopListUpdateCallback = object : ListUpdateCallback {
   override fun onInserted(position: Int, count: Int) {}
   override fun onRemoved(position: Int, count: Int) {}
   override fun onMoved(fromPosition: Int, toPosition: Int) {}
   override fun onChanged(position: Int, count: Int, payload: Any?) {}
}