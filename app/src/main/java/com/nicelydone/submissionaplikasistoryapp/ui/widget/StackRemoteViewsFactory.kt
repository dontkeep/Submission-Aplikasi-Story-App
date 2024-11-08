package com.nicelydone.submissionaplikasistoryapp.ui.widget

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.nicelydone.submissionaplikasistoryapp.R
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiConfig
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.model.connection.responses.ListStoryItem
import kotlinx.coroutines.runBlocking

class StackRemoteViewsFactory(
   private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

   private var storyItems = mutableListOf<ListStoryItem?>()
   private lateinit var apiService: ApiServices
   private val sharedPreferences: SharedPreferences = context.getSharedPreferences("session_preferences", Context.MODE_PRIVATE)

   override fun onCreate() {
      Log.d("StackRemoteViewsFactory Widget", "onCreate called")
      val token = sharedPreferences.getString("token", null) ?: ""
      Log.d("StackRemoteViewsFactory Widget", "Token: $token")
      apiService = ApiConfig(token).getApiService()
      onDataSetChanged()
   }

   override fun onDataSetChanged() {
      runBlocking {
         val token = sharedPreferences.getString("token", null) ?: ""
         if (token != null) {
//            val result = apiService.getAllStories("Bearer $token", 1, 15)
            val result = apiService.getAllStories(1, 15)
            if (result.message == "Stories fetched successfully") {
               result.listStory?.let { storyItems.addAll(it.toCollection(mutableListOf())) }
            } else {
               storyItems.clear()
               Log.e("StackRemoteViewsFactory", "Error fetching stories: ${result.message}")
            }
         } else {
            storyItems.clear()
            Log.e("StackRemoteViewsFactory", "Token is null")
         }
      }
   }

   override fun onDestroy() {
      storyItems.clear()
   }

   override fun getCount(): Int = storyItems.size

   override fun getViewAt(p0: Int): RemoteViews {
      val views = RemoteViews(context.packageName, R.layout.story_app_widget)

      if (storyItems.isEmpty()) {
         views.setViewVisibility(R.id.empty_view, View.VISIBLE)
         views.setViewVisibility(R.id.stack_view, View.GONE)
         return views
      }

      views.setViewVisibility(R.id.empty_view, View.GONE)
      views.setViewVisibility(R.id.stack_view, View.VISIBLE)

      if (p0 in 0 until storyItems.size) {
         val story = storyItems[p0]
         val itemViews = RemoteViews(context.packageName, R.layout.widget_item)

         val imageUrl = story?.photoUrl
         val bitmap = imageUrl?.let { getBitmapFromUrl(context, it) }

         if (bitmap != null) {
            itemViews.setImageViewBitmap(R.id.imageItem, bitmap)
         } else {
            itemViews.setImageViewResource(R.id.imageItem, R.drawable.placeholder)
         }

         return itemViews
      } else {
         Log.e("StackRemoteViewsFactory", "Index out of bounds: $p0, size: ${storyItems.size}")
         return views
      }
   }

   override fun getLoadingView(): RemoteViews {
      return RemoteViews(context.packageName, R.layout.widget_loading)
   }

   override fun getViewTypeCount(): Int = 1

   override fun getItemId(p0: Int): Long = p0.toLong()

   override fun hasStableIds(): Boolean = true

   private fun getBitmapFromUrl(context: Context, url: String): Bitmap? {
      return try {
         Log.d("StackRemoteViewsFactory", "Fetching image from URL: $url")
         Glide.with(context)
            .asBitmap()
            .load(url)
            .submit()
            .get()
      } catch (e: Exception) {
         null
      }
   }
}