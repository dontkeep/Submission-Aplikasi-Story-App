package com.nicelydone.submissionaplikasistoryapp.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
   @PrimaryKey
   @ColumnInfo(name = "id")
   val id: String,
   @ColumnInfo(name = "prevKey")
   val prevKey: Int?,
   @ColumnInfo(name = "nextKey")
   val nextKey: Int?
)
