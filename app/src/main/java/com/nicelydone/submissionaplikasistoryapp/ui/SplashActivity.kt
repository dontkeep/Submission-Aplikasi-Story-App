package com.nicelydone.submissionaplikasistoryapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
   private lateinit var binding : ActivitySplashBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      binding = ActivitySplashBinding.inflate(layoutInflater)
      setContentView(binding.root)

      lifecycleScope.launch {
         delay(2000)
         val sharedPreferences = EncryptedSharedPreferences.create(
            "session_preferences",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
         )

         val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

         if (isLoggedIn) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
         } else {
            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
         }

         finish()
      }
   }
}