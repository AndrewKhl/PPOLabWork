package com.example.projectonppo.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import com.example.projectonppo.Manager
import com.example.projectonppo.Models.User
import com.example.projectonppo.R
import com.example.projectonppo.Listener.SettingsLoader


class UserFragment: Fragment() {

    var editName: EditText? = null
    var editNickname: EditText? = null
    var editEmail: EditText? = null
    var editPhone: EditText? = null

    private var changeProfileStatus: Boolean = false
    private var manager = Manager.dataBase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    private fun setUserInfo(currentUser: User?){
        val viewName: TextView = view!!.findViewById(R.id.viewName)
        val viewNickname: TextView = view!!.findViewById(R.id.viewNickname)
        val viewEmail: TextView = view!!.findViewById(R.id.viewEmail)
        val viewPhone: TextView = view!!.findViewById(R.id.viewPhone)

        viewName.text = currentUser?.name ?: "No name"
        viewNickname.text = currentUser?.nickname ?: "No nickname"
        viewEmail.text = currentUser?.email ?: "No email"
        viewPhone.text = currentUser?.phone ?: ""
    }

    private fun setUserEdit(currentUser: User?){
        editName?.text = SpannableStringBuilder(currentUser?.name)
        editNickname?.text = SpannableStringBuilder(currentUser?.nickname)
        editEmail?.text = SpannableStringBuilder(currentUser?.email)
        editPhone?.text = SpannableStringBuilder(currentUser?.phone)
    }

    private fun getUserChange(): User {
        val name = editName?.text.toString().trim()
        val nickname = editNickname?.text.toString().trim()
        val email = editEmail?.text.toString().trim()
        val phone = editPhone?.text.toString().trim()
        return User(name, nickname, email, phone)
    }

    private fun setValidationToEdit(){
        editNickname!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (editNickname!!.text.toString().isEmpty())
                    editNickname!!.error = "Nickname is required"
            }
        }

        editEmail?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                val email: String  = editEmail!!.text.toString()

                if (email.isEmpty()){
                    editEmail!!.error = "Email is required"
                    return
                }

                val emailPattern = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}\$".toRegex()

                if (!email.matches(emailPattern))
                    editEmail!!.error = "Invalid email"
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editName = view.findViewById(R.id.editName)
        editNickname = view.findViewById(R.id.editNickname)
        editEmail = view.findViewById(R.id.editEmail)
        editPhone = view.findViewById(R.id.editPhone)

        val bthSave = view.findViewById<Button>(R.id.save_btn)
        val viewSwitcher = view.findViewById<ViewSwitcher>(R.id.switcherEdit)
        changeProfileStatus = false

        setValidationToEdit()

        //var newUser = User("Andrew", "Red", "red@mail.ru", "12331231", "123456")

        bthSave.setOnClickListener {
            if (!changeProfileStatus) {
                setUserEdit(manager.getCurrentUser())
                bthSave.text = "Save"
                changeProfileStatus = true
            }
            else {
                manager.changeUser(getUserChange())
                bthSave.text = "Change"
                changeProfileStatus = false

                setUserInfo(manager.getCurrentUser())
            }
            viewSwitcher.showNext()
        }

        if (manager.getCurrentUser() == null){
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Downloading user...")
            progressDialog.setCancelable(false)

            SettingsLoader(object : SettingsLoader.LoadListener
            {
                override fun onPreExecute() {
                    progressDialog.show()
                }

                override fun onPostExecute() {
                    setUserInfo(manager.getCurrentUser())
                    progressDialog.dismiss()
                }
            }, email = "red@mail.ru", password = "123456").execute()
        }
        else
            setUserInfo(manager.getCurrentUser())
    }
}