package com.example.projectonppo.validations

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ValidationForPhone(val editPhone: EditText?) : TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val email: String  = editPhone?.text.toString()
        val emailPattern = "^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){10,14}(\\s*)?\$".toRegex()

        if (!email.matches(emailPattern) && email.isNotEmpty())
            editPhone?.error = "Invalid number of phone"
    }
}
