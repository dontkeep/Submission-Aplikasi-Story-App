package com.nicelydone.submissionaplikasistoryapp.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nicelydone.submissionaplikasistoryapp.R

class MyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count)
        if (s.toString().length < 8) {
            setError(context.getString(R.string.password_format), null)
        } else {
            error = null
        }
    }
}