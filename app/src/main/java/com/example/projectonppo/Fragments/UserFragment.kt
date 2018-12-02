package com.example.projectonppo.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectonppo.Databases.Manager
import com.example.projectonppo.Listeners.SettingsLoader
import com.example.projectonppo.Models.User
import com.example.projectonppo.R
import com.example.projectonppo.Validations.ValidationForEmail
import com.example.projectonppo.Validations.ValidationForPhone
import com.example.projectonppo.Validations.ValidationForRequired
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment: Fragment() {
    private var manager = Manager.dataBase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!manager.userInSystem()){
            Toast.makeText(context, "Please, enter the system", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
            return null
        }
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
        editNameChange?.text = SpannableStringBuilder(currentUser?.name)
        editNicknameChange?.text = SpannableStringBuilder(currentUser?.nickname)
        editEmailChange?.text = SpannableStringBuilder(currentUser?.email)
        editPhoneChange?.text = SpannableStringBuilder(currentUser?.phone)
    }

    private fun getUserChange(): User {
        val name = editNameChange?.text.toString().trim()
        val nickname = editNicknameChange?.text.toString().trim()
        val email = editEmailChange?.text.toString().trim()
        val phone = editPhoneChange?.text.toString().trim()
        return User(name, nickname, email, phone)
    }

    private fun setValidationToEdit(){
        editNicknameChange?.addTextChangedListener(ValidationForRequired(editNicknameChange, "Nickname"))
        editEmailChange?.addTextChangedListener(ValidationForEmail(editEmailChange))
        editPhoneChange?.addTextChangedListener(ValidationForPhone(editPhoneChange))
    }

    private fun checkEditOnError(): Boolean{
        if ((editEmailChange.error != null) || (editEmailChange.text.isEmpty()))
            return false
        if ((editNicknameChange.error != null) || (editNicknameChange.text.isEmpty()))
            return false
        if ((editNameChange.error != null) || (editNameChange.text.isEmpty()))
            return false
        if ((editPhoneChange.error != null) || (editPhoneChange.text.isEmpty()))
            return false
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var changeProfileStatus = false

        setValidationToEdit()

        btnSave.setOnClickListener {
            if (!changeProfileStatus) {
                setUserEdit(manager.getCurrentUser())
                btnSave.text = "Save"
                changeProfileStatus = true
            }
            else {
                if (checkEditOnError()) {
                    saveChangeUser()
                    btnSave.text = "Change"
                    changeProfileStatus = false
                }
                else
                    Toast.makeText(context, "Correct the mistakes", Toast.LENGTH_SHORT).show()
            }

            viewSwitcherChange.showNext()
        }

        waitUserLoad()
    }

    private fun waitUserLoad(){
        if (manager.getCurrentUser() != null) {
            setUserInfo(manager.getCurrentUser())
            return
        }

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Load user...")
        progressDialog.setCancelable(false)

        SettingsLoader(object : SettingsLoader.LoadListener
        {
            override fun onPreExecute() {
                progressDialog.show()
            }

            override fun onPostExecute() {
                progressDialog.dismiss()
                setUserInfo(manager.getCurrentUser())
            }

            override fun doInBackground() {
                while(true){
                    if (manager.getCurrentUser() != null)
                        break
                }
            }
        }).execute()
    }

    override fun onDestroyView() {
        saveChangeUser()
        super.onDestroyView()
    }

    private fun saveChangeUser(){
        val newUser = getUserChange()
        if (!compare(newUser, manager.getCurrentUser())) {

            val dialog = AlertDialog.Builder(context!!)
            dialog.setMessage("Data has been changed, do you want to save it?")
            dialog.setTitle("Warning")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Yes") { _, _ ->
                run {
                    manager.changeUser(newUser)
                    setUserInfo(manager.getCurrentUser())
                }
            }

            dialog.setNegativeButton("No") { _, _ -> }
            dialog.show()
        }
    }

    private fun compare(a: User, b: User?): Boolean{
        if (b == null)
            return false
        if (a.email != b.email)
            return false
        if (a.phone != b.phone)
            return false
        if (a.nickname != b.nickname)
            return false
        if (a.name != b.name)
            return false
        return true
    }
}