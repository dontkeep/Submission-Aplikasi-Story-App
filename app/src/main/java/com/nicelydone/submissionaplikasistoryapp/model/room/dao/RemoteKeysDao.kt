package com.nicelydone.submissionaplikasistoryapp.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.RemoteKeys

@Dao
interface RemoteKeysDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKeys>)

   @Query("SELECT * FROM remote_keys WHERE id = :id")
   suspend fun remoteKeysStoryId(id: String): RemoteKeys?

   @Query("DELETE FROM remote_keys")
   suspend fun clearRemoteKeys()
}