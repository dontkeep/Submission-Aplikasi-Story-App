package com.nicelydone.submissionaplikasistoryapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.nicelydone.submissionaplikasistoryapp.R
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityMainBinding
import com.nicelydone.submissionaplikasistoryapp.helper.MainActivityModule
import com.nicelydone.submissionaplikasistoryapp.helper.MainActivityModule.provideSharedPreferencesForZeroArgConstructor
import com.nicelydone.submissionaplikasistoryapp.helper.MyApplication
import com.nicelydone.submissionaplikasistoryapp.ui.adapter.StoryListAdapter
import com.nicelydone.submissionaplikasistoryapp.viewmodel.StoryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor(@MainActivityModule.ZeroArgConstructor val sharedPreferences: SharedPreferences) : AppCompatActivity() {
   constructor() : this(sharedPreferences = provideSharedPreferencesForZeroArgConstructor(MyApplication.appContext))

   private lateinit var binding: ActivityMainBinding
   private lateinit var adapter: StoryListAdapter
   private val viewModel: StoryListViewModel by viewModels()

   private val uploadActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == RESULT_OK) {
         refreshStories()
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
      binding.rvStories.adapter = adapter

      refreshStories()

      binding.floatingActionButton.setOnClickListener {
         val intent = Intent(this, UploadActivity::class.java)
         uploadActivityResultLauncher.launch(intent)
      }

      viewModel.isLoading.observe(this) { isLoading ->
         binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
      }
   }

   private fun refreshStories() {
      viewModel.getStories()
      viewModel.stories.observe(this) { stories ->
         adapter.submitList(stories)
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
         }else -> super.onOptionsItemSelected(item)
      }
   }

}