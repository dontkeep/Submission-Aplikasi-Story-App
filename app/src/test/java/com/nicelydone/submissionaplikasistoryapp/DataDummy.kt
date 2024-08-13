package com.nicelydone.submissionaplikasistoryapp

import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.ListStoryItem

object DataDummy {
   fun generateDummyStoryResponse(): List<ListStoryItem> {
      val items: MutableList<ListStoryItem> = arrayListOf()
      for (i in 0 until 100) {
         val story = ListStoryItem(
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