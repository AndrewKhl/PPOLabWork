package com.example.projectonppo.validations

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ValidationForUrl(private val editUrl: EditText) : TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val email: String  = editUrl.text.toString()

        val emailPattern = "([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?".toRegex()

        if ((!email.matches(emailPattern)) and (email.isNotEmpty()))
            editUrl.error = "Invalid url"
        }
}
