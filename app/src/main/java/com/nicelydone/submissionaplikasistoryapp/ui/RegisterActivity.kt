package com.nicelydone.submissionaplikasistoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityRegisterBinding
import com.nicelydone.submissionaplikasistoryapp.helper.ViewModelFactory
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiConfig
import com.nicelydone.submissionaplikasistoryapp.model.connection.ApiServices
import com.nicelydone.submissionaplikasistoryapp.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
   private lateinit var binding: ActivityRegisterBinding
   private lateinit var apiServices: ApiServices
   private val registerViewModel: RegisterViewModel by viewModels {
      ViewModelFactory(apiServices)
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityRegisterBinding.inflate(layoutInflater)
      setContentView(binding.root)
      enableEdgeToEdge()

      val token = getSharedPreferences("session_preferences", Context.MODE_PRIVATE)
         .getString("token", null)
      val apiConfig = ApiConfig(token ?: "")
      apiServices = apiConfig.getApiService()


      binding.signupButton.setOnClickListener {
         val name = binding.nameEditText.text.toString()
         val email = binding.emailEditText.text.toString()
         val password = binding.passwordEditText.text.toString()
         registerViewModel.register(name, email, password)
      }

      registerViewModel.isLoading.observe(this) {
         binding.loadingAnimation.visibility = if (it) View.VISIBLE else View.GONE
         binding.signupButton.isEnabled = !it
         binding.nameEditTextLayout.isEnabled = !it
         binding.emailEditTextLayout.isEnabled = !it
         binding.passwordEditTextLayout.isEnabled = !it
      }

      registerViewModel.registerResult.observe(this) { result ->
         result.onSuccess {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            finish()
         }.onFailure {
            Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
         }
      }

      startAnimation()
   }

   private fun startAnimation() {
      ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
         setDuration(6000)
         repeatMode = ObjectAnimator.REVERSE
         repeatCount = ObjectAnimator.INFINITE
      }.start()

      val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
      val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(200)
      val edName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(200)
      val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
      val edEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
      val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
      val edPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
      val signUpButton = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(200)

      title.interpolator = DecelerateInterpolator()
      name.interpolator = DecelerateInterpolator()
      email.interpolator = DecelerateInterpolator()
      password.interpolator = DecelerateInterpolator()
      signUpButton.interpolator = DecelerateInterpolator()

      AnimatorSet().apply {
         playSequentially(title, name, edName, email, edEmail, password, edPassword, signUpButton)
      }.start()
   }
}