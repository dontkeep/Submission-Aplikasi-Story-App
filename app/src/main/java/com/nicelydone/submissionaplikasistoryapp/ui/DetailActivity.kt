package com.nicelydone.submissionaplikasistoryapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityDetailBinding
import com.nicelydone.submissionaplikasistoryapp.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
   private lateinit var binding: ActivityDetailBinding
   val detailViewModel: DetailViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityDetailBinding.inflate(layoutInflater)
      setContentView(binding.root)

      val storyId = intent.getStringExtra("story_id") ?: ""
      detailViewModel.getStoryDetail(storyId)

      detailViewModel.storyDetail.observe(this) { story ->
         Glide.with(this)
            .load(story?.photoUrl) // Use photoUrl from StoryEntity
            .into(binding.ivDetailPhoto)
         binding.tvDetailTitle.text = story?.name
         binding.tvDetailDescription.text = story?.description
      }
   }
}