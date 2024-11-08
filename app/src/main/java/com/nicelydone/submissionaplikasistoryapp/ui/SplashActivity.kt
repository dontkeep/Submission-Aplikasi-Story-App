package com.nicelydone.submissionaplikasistoryapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
   private lateinit var binding : ActivitySplashBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      binding = ActivitySplashBinding.inflate(layoutInflater)
      setContentView(binding.root)

      lifecycleScope.launch {
         delay(2000)
         try {
            val sharedPreferences= getSharedPreferences("session_preferences", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

            if (isLoggedIn) {
               startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
               startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
            }

            finish()
         } catch (e: Exception) {
            Log.e("SplashActivity", "Error accessing SharedPreferences", e)
         }
      }
   }
}