package com.example.projectonppo.validations

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ValidationForRequired(val editNickname: EditText?, val message:String = ""): TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val email: String  = editNickname?.text.toString()

        if (email.isEmpty()){
            editNickname?.error = "$message is required"
            return
        }
    }
}