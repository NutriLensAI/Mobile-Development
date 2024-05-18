package com.dicoding.picodiploma.loginwithanimation.view.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.loginwithanimation.R
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var passwordValidationListener: PasswordValidationListener? = null
    private lateinit var errorDrawable: Drawable

    init {
        isFocusableInTouchMode = true
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        // Initialize any custom attributes here
        // For example, you can define custom attributes to set password requirements
        // and read them from the AttributeSet

        // Set up error indicator drawable
        errorDrawable = AppCompatResources.getDrawable(context, R.drawable.error_indicator)!!

        // Set the text change listener for password validation
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun validatePassword(password: String) {
        // Implement your password validation logic here
        // For example, check password length, complexity, etc.
        val isValid = password.length >= 8 // Example: Minimum length of 8 characters

        if (isValid) {
            // Clear error message and indicator
            error = null
            passwordValidationListener?.onPasswordValidated(true)
        } else {
            // Set error message and indicator
            error = context.getString(R.string.password_error)
            passwordValidationListener?.onPasswordValidated(false)
        }

        // Set the visibility of the error icon based on validation result
        setErrorIconVisible(!isValid)
    }

    private fun setErrorIconVisible(visible: Boolean) {
        val parent = parent
        if (parent is TextInputLayout) {
            parent.isErrorEnabled = visible
            parent.errorIconDrawable = if (visible) errorDrawable else null
        }
    }

    fun setPasswordValidationListener(listener: PasswordValidationListener) {
        this.passwordValidationListener = listener
    }

    interface PasswordValidationListener {
        fun onPasswordValidated(isValid: Boolean)
    }
}