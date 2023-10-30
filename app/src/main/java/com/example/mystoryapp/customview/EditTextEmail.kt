package com.example.mystoryapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.mystoryapp.R

class EditTextEmail : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()
                if (!isValid) {
                    setError(context.getString(R.string.email_validation), null)
                } else {
                    error = null
                }
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
    }
}