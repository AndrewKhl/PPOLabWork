package com.example.projectonppo.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectonppo.listeners.SettingsLoader
import com.example.projectonppo.MainActivity
import com.example.projectonppo.managers.databases.Manager
import com.example.projectonppo.models.User
import com.example.projectonppo.R
import com.example.projectonppo.validations.ValidationForEmail
import com.example.projectonppo.validations.ValidationForPassword
import com.example.projectonppo.validations.ValidationForRequired
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment: Fragment() {
    private val manager = Manager.dataBase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValidationToEdit()

        btnAddUser.setOnClickListener {
            if (checkEditOnError())
            {
                val email = editEmailRegistration.text.toString().trim()
                val nickname = editNicknameRegistration.text.toString().trim()
                val password = editPasswordRegistration.text.toString().trim()
                val confPassword = editConfPasswordRegistration.text.toString().trim()

                if (password != confPassword) {
                    Toast.makeText(context, resources.getText(R.string.equals_password), Toast.LENGTH_SHORT).show()
                    editPasswordRegistration.text.clear()
                    editConfPasswordRegistration.text.clear()
                }
                else
                {
                    val newUser = User(email = email, nickname = nickname, password = password)
                    val progressDialog = ProgressDialog(context)
                    progressDialog.setMessage(resources.getText(R.string.registration_user))
                    progressDialog.setCancelable(false)

                    SettingsLoader(object : SettingsLoader.LoadListener
                    {
                        override fun onPreExecute() {
                            manager.registerUser(newUser)
                            progressDialog.show()
                        }

                        override fun onPostExecute() {
                            progressDialog.dismiss()
                            if (manager.successRegistration == true){
                                startActivity(Intent(context, MainActivity::class.java))
                                activity?.finish()
                            }
                            else
                                Toast.makeText(context, resources.getText(R.string.taken_email), Toast.LENGTH_SHORT).show()
                        }

                        override fun doInBackground() {
                            while(true){
                                if (manager.successRegistration == false)
                                    break

                                if ((manager.successRegistration == true) and (manager.getCurrentUser() != null))
                                    break
                            }
                        }
                    }).execute()
                }
            }
            else
                Toast.makeText(context, resources.getText(R.string.fill_correctly_fields), Toast.LENGTH_SHORT).show()
        }

        btnBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun setValidationToEdit(){
        editEmailRegistration.addTextChangedListener(ValidationForEmail(editEmailRegistration))
        editNicknameRegistration.addTextChangedListener(ValidationForRequired(editNicknameRegistration, resources.getText(R.string.nicknameWithoutColons).toString()))
        editPasswordRegistration.addTextChangedListener(ValidationForPassword(editPasswordRegistration))
        editConfPasswordRegistration.addTextChangedListener(ValidationForPassword(editPasswordRegistration))
    }

    private fun checkEditOnError(): Boolean{
        if ((editEmailRegistration.error != null) || (editEmailRegistration.text.isEmpty()))
            return false
        if ((editNicknameRegistration.error != null) || (editNicknameRegistration.text.isEmpty()))
            return false
        if ((editPasswordRegistration.error != null) || (editPasswordRegistration.text.isEmpty()))
            return false
        if ((editConfPasswordRegistration.error != null) || (editConfPasswordRegistration.text.isEmpty()))
            return false
        return true
    }
}


