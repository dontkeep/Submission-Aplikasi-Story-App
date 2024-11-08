package com.nicelydone.submissionaplikasistoryapp.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryAppWidgetService : RemoteViewsService() {
   override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
      return StackRemoteViewsFactory(this.applicationContext)
   }
}