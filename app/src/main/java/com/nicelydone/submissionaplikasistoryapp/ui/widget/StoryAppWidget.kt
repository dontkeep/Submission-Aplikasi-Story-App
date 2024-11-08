package com.nicelydone.submissionaplikasistoryapp.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.nicelydone.submissionaplikasistoryapp.R

class StoryAppWidget : AppWidgetProvider() {
   override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray
   ) {
      for (appWidgetId in appWidgetIds) {
         updateAppWidget(context, appWidgetManager, appWidgetId)
      }
   }

   override fun onReceive(context: Context, intent: Intent) {
      super.onReceive(context, intent)

      if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
         val appWidgetManager = AppWidgetManager.getInstance(context)
         val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
         if (appWidgetIds != null) {
            onUpdate(context, appWidgetManager, appWidgetIds)
         }
      }
   }

   override fun onEnabled(context: Context) {
      // Enter relevant functionality for when the first widget is created
   }

   override fun onDisabled(context: Context) {
      // Enter relevant functionality for when the last widget is disabled
   }
}

internal fun updateAppWidget(
   context: Context,
   appWidgetManager: AppWidgetManager,
   appWidgetId: Int
) {
   val intent = Intent(context, StoryAppWidgetService::class.java)
   val views = RemoteViews(context.packageName, R.layout.story_app_widget).apply {
      setRemoteAdapter(R.id.stack_view, intent)
      setEmptyView(R.id.stack_view, R.id.empty_view)
   }
   appWidgetManager.updateAppWidget(appWidgetId, views)
}