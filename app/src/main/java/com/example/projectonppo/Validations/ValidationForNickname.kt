package com.example.projectonppo.Validations

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ValidationForNickname(val editNickname: EditText?): TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val email: String  = editNickname?.text.toString()

        if (email.isEmpty()){
            editNickname?.error = "Nickname is required"
            return
        }
    }
}