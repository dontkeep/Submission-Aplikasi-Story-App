package com.nicelydone.submissionaplikasistoryapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.nicelydone.submissionaplikasistoryapp.R
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityMainBinding
import com.nicelydone.submissionaplikasistoryapp.helper.MainActivityModule
import com.nicelydone.submissionaplikasistoryapp.helper.MainActivityModule.provideSharedPreferencesForZeroArgConstructor
import com.nicelydone.submissionaplikasistoryapp.helper.MyApplication
import com.nicelydone.submissionaplikasistoryapp.ui.adapter.LoadingStateAdapter
import com.nicelydone.submissionaplikasistoryapp.ui.adapter.StoryListAdapter
import com.nicelydone.submissionaplikasistoryapp.viewmodel.StoryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor(@MainActivityModule.ZeroArgConstructor val sharedPreferences: SharedPreferences) : AppCompatActivity() {
   constructor() : this(sharedPreferences = provideSharedPreferencesForZeroArgConstructor(MyApplication.appContext))

   private lateinit var binding: ActivityMainBinding
   private lateinit var adapter: StoryListAdapter
   private val viewModel: StoryListViewModel by viewModels()


   private val uploadActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
   ) { result ->
      if (result.resultCode == RESULT_OK) {
         refreshStories()
         binding.rvStories.post {
            binding.rvStories.smoothScrollToPosition(0)
         }
      } else {
         Log.d("MainActivity", "Upload failed or was cancelled")
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)

      setSupportActionBar(binding.topAppBar)

      val layoutManager = LinearLayoutManager(this)
      binding.rvStories.layoutManager = layoutManager

      adapter = StoryListAdapter()
      val loadingStateAdapter = LoadingStateAdapter { adapter.retry() }
      binding.rvStories.adapter = adapter.withLoadStateFooter(
         footer = loadingStateAdapter
      )

      refreshStories()

      binding.floatingActionButton.setOnClickListener {
         val intent = Intent(this, UploadActivity::class.java)
         uploadActivityResultLauncher.launch(intent)
      }
   }

   private fun refreshStories() {
      lifecycleScope.launch {
         viewModel.refreshStories()
         viewModel.stories.observe(this@MainActivity) { pagingData ->
            if (pagingData != null) {
               Log.d("MainActivity", "Refreshing Stories with PagingData: $pagingData")
               adapter.submitData(lifecycle, pagingData)
            }
         }
      }

      lifecycleScope.launch {
         adapter.loadStateFlow.collect { loadState ->
            val errorState = loadState.refresh as? LoadState.Error
               ?: loadState.append as? LoadState.Error
               ?: loadState.prepend as? LoadState.Error

            errorState?.let {
               Toast.makeText(
                  this@MainActivity,
                  "Please Connect to Internet",
                  Toast.LENGTH_LONG
               ).show()
            }
         }
      }
   }

   override fun onCreateOptionsMenu(menu: Menu): Boolean {
      menuInflater.inflate(R.menu.menu_main, menu)
      return true
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return when (item.itemId) {
         R.id.profile -> {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            true
         }
         R.id.logoutButton -> {
            val sharedPreferences = EncryptedSharedPreferences.create(
               "session_preferences",
               MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
               applicationContext,
               EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
               EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val editor = sharedPreferences.edit()
            editor.remove("token") // Clear the token
            editor.putBoolean("IS_LOGGED_IN", false)
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            true
         }
         R.id.action_open_map -> {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
            true
         }
         else -> super.onOptionsItemSelected(item)
      }
   }
}