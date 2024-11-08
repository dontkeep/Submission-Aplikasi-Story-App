package com.nicelydone.submissionaplikasistoryapp.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.nicelydone.submissionaplikasistoryapp.R

class MyEmailEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle): AppCompatEditText(context, attrs, defStyleAttr) {

   override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      super.onTextChanged(s, start, before, count)
      if (s.isNullOrEmpty()) {
         error = null
      } else if (!isValidEmail(s.toString())) {
         setError(context.getString(R.string.email_format), null)
      } else {
         error = null
      }
   }

   private fun isValidEmail(email: String): Boolean {
      return Patterns.EMAIL_ADDRESS.matcher(email).matches()
   }
}