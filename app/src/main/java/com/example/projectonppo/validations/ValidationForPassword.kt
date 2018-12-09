package com.example.projectonppo.validations

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ValidationForPassword(val editPassword: EditText): TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val email: String  = editPassword.text.toString()

        if (email.isEmpty()){
            editPassword.error = "Password is required"
            return
        }

        if (email.length < 6){
            editPassword.error = "Password must be longer than 5 characters"
            return
        }
    }
}