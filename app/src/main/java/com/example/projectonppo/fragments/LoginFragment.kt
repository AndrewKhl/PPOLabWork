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
import com.example.projectonppo.R
import com.example.projectonppo.validations.ValidationForEmail
import com.example.projectonppo.validations.ValidationForRequired
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: Fragment() {
    val manager = Manager.dataBase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        manager.signOut()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValidationToEdit()

        //editEmailLogin.setText("red@mail.ru")
        //editPasswordLogin.setText("123456")

        bthLogin.setOnClickListener {
            if (checkEditOnError()){
                val email = editEmailLogin?.text.toString().trim()
                val password = editPasswordLogin?.text.toString().trim()

                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage(resources.getText(R.string.check_user_data))
                progressDialog.setCancelable(false)

                SettingsLoader(object : SettingsLoader.LoadListener
                {
                    override fun onPreExecute() {
                        manager.signUser(email, password)
                        progressDialog.show()
                    }

                    override fun onPostExecute() {
                        progressDialog.dismiss()
                        if (manager.successSign == true) {
                            startActivity(Intent(context, MainActivity::class.java))
                            activity?.finish()
                        }
                        else
                            Toast.makeText(context, resources.getText(R.string.invalid_email_or_password), Toast.LENGTH_SHORT).show()
                    }

                    override fun doInBackground() {
                        while(true){
                            if (manager.successSign == false)
                                break

                            if ((manager.successSign == true) and (manager.getCurrentUser() != null))
                                break
                        }
                        manager.downloadAvatarFromDatabase()
                        while(true){
                            if (manager.successDownloadAvatar != null)
                                break
                        }

                    }
                }).execute()
            }
            else
                Toast.makeText(context, resources.getText(R.string.fill_correctly_fields), Toast.LENGTH_SHORT).show()
        }

        btnRegistration.setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
        }
    }

    private fun setValidationToEdit(){
        editEmailLogin?.addTextChangedListener(ValidationForEmail(editEmailLogin))
        editPasswordLogin?.addTextChangedListener(ValidationForRequired(editPasswordLogin, resources.getText(R.string.password).toString()))
    }

    private fun checkEditOnError(): Boolean{
        if ((editEmailLogin?.error != null) || (editEmailLogin.text.isEmpty()))
            return false
        if ((editPasswordLogin.error != null) || (editPasswordLogin.text.isEmpty()))
            return false
        return true
    }
}


