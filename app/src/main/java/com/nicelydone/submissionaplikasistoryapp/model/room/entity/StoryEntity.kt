package com.nicelydone.submissionaplikasistoryapp.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
   @PrimaryKey
   @ColumnInfo(name = "id")
   val id: String,
   @ColumnInfo(name = "photoUrl")
   val photoUrl: String,
   @ColumnInfo(name = "createdAt")
   val createdAt: String,
   @ColumnInfo(name = "name")
   val name: String,
   @ColumnInfo(name = "description")
   val description: String,
   @ColumnInfo(name = "lon")
   val lon: Double,
   @ColumnInfo(name = "lat")
   val lat: Double
)
