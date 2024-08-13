package com.nicelydone.submissionaplikasistoryapp.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nicelydone.submissionaplikasistoryapp.model.room.dao.RemoteKeysDao
import com.nicelydone.submissionaplikasistoryapp.model.room.dao.StoryDao
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.RemoteKeys
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class ItemDatabase: RoomDatabase() {
   abstract fun storyDao(): StoryDao
   abstract fun remoteKeysDao(): RemoteKeysDao

   companion object {
      @Volatile
      private var INSTANCE: ItemDatabase? = null

      @JvmStatic
      fun getDatabase(context: Context): ItemDatabase {
         return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
               context.applicationContext,
               ItemDatabase::class.java, "story_database"
            )
               .fallbackToDestructiveMigration()
               .build()
               .also { INSTANCE = it }
         }
      }
   }
}