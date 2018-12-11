package com.example.projectonppo.validations

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ValidationForEmail(val editEmail: EditText) : TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val email: String  = editEmail.text.toString()

        if (email.isEmpty()){
            editEmail.error = "Email is required"
            return
        }

        val emailPattern = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}\$".toRegex()

        if (!email.matches(emailPattern))
            editEmail.error = "Invalid email"
        }
}
