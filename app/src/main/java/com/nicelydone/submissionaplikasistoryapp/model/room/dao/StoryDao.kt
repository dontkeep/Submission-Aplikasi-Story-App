package com.nicelydone.submissionaplikasistoryapp.model.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity


@Dao
interface StoryDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertAll(stories: List<StoryEntity>)

   @Query("SELECT * FROM stories")
   fun getStories(): PagingSource<Int, StoryEntity>

   @Query("SELECT * FROM stories WHERE id = :storyId LIMIT 1")
   suspend fun getStoryById(storyId: String): StoryEntity?

   @Query("DELETE FROM stories")
   suspend fun clearStories()
}