package com.nicelydone.submissionaplikasistoryapp

import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.ListStoryItem
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity

object DataDummy {
   fun generateDummyStoryResponse(): List<StoryEntity> {
      val items: MutableList<StoryEntity> = arrayListOf()
      for (i in 0 until 100) {
         val story = StoryEntity(
            id = i.toString(),
            createdAt = "createdAt + $i",
            name = "name $i",
            description = "description $i",
            lon = i.toDouble(),
            photoUrl = "randomPhotoUrl $i",
            lat = i.toDouble()
         )
         items.add(story)
      }
      return items
   }
}