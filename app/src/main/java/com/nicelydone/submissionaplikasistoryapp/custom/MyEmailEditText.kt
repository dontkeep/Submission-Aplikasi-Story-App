package com.nicelydone.submissionaplikasistoryapp.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText

class MyEmailEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle): AppCompatEditText(context, attrs, defStyleAttr) {

   override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      super.onTextChanged(s, start, before, count)
      if (s.isNullOrEmpty()) {
         error = null // Clear error if the field is empty
      } else if (!isValidEmail(s.toString())) {
         setError("Invalid Email Format", null)
      } else {
         error = null // Clear error if the email is valid
      }
   }

   private fun isValidEmail(email: String): Boolean {
      return Patterns.EMAIL_ADDRESS.matcher(email).matches()
   }
}