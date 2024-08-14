package com.nicelydone.submissionaplikasistoryapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nicelydone.submissionaplikasistoryapp.databinding.ItemStoryBinding
import com.nicelydone.submissionaplikasistoryapp.model.room.entity.StoryEntity
import com.nicelydone.submissionaplikasistoryapp.ui.DetailActivity

class StoryListAdapter: PagingDataAdapter<StoryEntity, StoryListAdapter.ViewHolder>(DIFF_CALLBACK) {
   class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
      fun bind(story: StoryEntity){
         with(binding) {
            Glide.with(root.context)
               .load(story.photoUrl)
               .into(ivItemPhoto)
            tvItemName.text = story.name
            val shortDescription = if ((story.description.length ?: 0) > 50) {
               "${story.description.substring(0, 50)}..."
            } else {
               story.description
            }
            tvItemDescription.text = shortDescription
         }
      }
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return ViewHolder(binding)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val story = getItem(position)
      story?.let { holder.bind(it) }

      holder.itemView.setOnClickListener {
         story?.let {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
               ActivityOptionsCompat.makeSceneTransitionAnimation(
                  holder.itemView.context as Activity,
                  androidx.core.util.Pair(holder.binding.ivItemPhoto, "detail_photo"),
                  androidx.core.util.Pair(holder.binding.tvItemName, "title"),
                  androidx.core.util.Pair(holder.binding.tvItemDescription, "description"),
               )

            intent.putExtra("story_id", story.id)
            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
         }
      }
   }

   companion object {
      val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>(){
         override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem.id == newItem.id
         }

         override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem == newItem
         }
      }
   }
}