package com.nicelydone.submissionaplikasistoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityLoginBinding
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiConfig
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
   private lateinit var binding: ActivityLoginBinding
   private lateinit var apiServices: ApiServices
   private val loginViewModel: LoginViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)
      enableEdgeToEdge()

      val token = getSharedPreferences("session_preferences", Context.MODE_PRIVATE)
         .getString("token", null)
      val apiConfig = ApiConfig(token ?: "")
      apiServices = apiConfig.getApiService()

      setAnimation()

      binding.loginButton.setOnClickListener {
         val email = binding.emailEditText.text.toString()
         val password = binding.passwordEditText.text.toString()
         loginViewModel.login(email, password)
      }

      loginViewModel.isLoading.observe(this) {
         binding.loadingAnimation.visibility = if (it) View.VISIBLE else View.GONE
         binding.emailEditTextLayout.isEnabled = !it
         binding.passwordEditTextLayout.isEnabled = !it
         binding.loginButton.isEnabled = !it
      }

      loginViewModel.loginResult.observe(this) { result ->
         result.onSuccess {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
         }.onFailure {
            Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun setAnimation() {
      ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
         setDuration(6000)
         repeatMode = ObjectAnimator.REVERSE
         repeatCount = ObjectAnimator.INFINITE
      }

      val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
      val desc = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(200)
      val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
      val edEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
      val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
      val edPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
      val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(200)

      AnimatorSet().apply {
         playSequentially(title, desc, email, edEmail, password, edPassword, loginButton)
      }.start()
   }
}