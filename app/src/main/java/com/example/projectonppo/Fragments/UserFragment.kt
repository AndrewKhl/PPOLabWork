package com.example.projectonppo.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import com.example.projectonppo.Listeners.SettingsLoader
import com.example.projectonppo.Manager
import com.example.projectonppo.Models.User
import com.example.projectonppo.R
import com.example.projectonppo.Validations.ValidationForEmail
import com.example.projectonppo.Validations.ValidationForPhone
import com.example.projectonppo.Validations.ValidationForRequired


class UserFragment: Fragment() {

    private var editName: EditText? = null
    private var editNickname: EditText? = null
    private var editEmail: EditText? = null
    private var editPhone: EditText? = null
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
        editNickname?.addTextChangedListener(ValidationForRequired(editNickname, "nickname"))
        editEmail?.addTextChangedListener(ValidationForEmail(editEmail))
        editPhone?.addTextChangedListener(ValidationForPhone(editPhone))
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editName = view.findViewById(R.id.editName)
        editNickname = view.findViewById(R.id.editNickname)
        editEmail = view.findViewById(R.id.editEmail)
        editPhone = view.findViewById(R.id.editPhone)

        val bthSave = view.findViewById<Button>(R.id.save_btn)
        val viewSwitcher = view.findViewById<ViewSwitcher>(R.id.switcherEdit)
        var changeProfileStatus = false

        setValidationToEdit()

        //var newUser = User("Andrew", "Red", "red@mail.ru", "12331231", "123456")

        bthSave.setOnClickListener {
            if (!changeProfileStatus) {
                setUserEdit(manager.getCurrentUser())
                bthSave.text = "Save"
                changeProfileStatus = true
            }
            else {
                val newUser = getUserChange()
                if (newUser != manager.getCurrentUser()){
                    manager.changeUser(getUserChange())
                    setUserInfo(manager.getCurrentUser())
                }

                bthSave.text = "Change"
                changeProfileStatus = false
            }

            viewSwitcher.showNext()
        }

        /*
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
        else*/
            setUserInfo(manager.getCurrentUser())
    }
}