package com.nicelydone.submissionaplikasistoryapp.helper

import android.content.Context
import com.nicelydone.submissionaplikasistoryapp.model.room.ItemDatabase
import com.nicelydone.submissionaplikasistoryapp.model.room.dao.RemoteKeysDao
import com.nicelydone.submissionaplikasistoryapp.model.room.dao.StoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
   @Provides
   @Singleton
   fun provideItemDatabase(@ApplicationContext context: Context): ItemDatabase {
      return ItemDatabase.getDatabase(context)
   }

   @Provides
   fun provideStoryDao(itemDatabase: ItemDatabase): StoryDao {
      return itemDatabase.storyDao()
   }

   @Provides
   fun provideRemoteKeysDao(itemDatabase: ItemDatabase): RemoteKeysDao {
      return itemDatabase.remoteKeysDao()
   }
}