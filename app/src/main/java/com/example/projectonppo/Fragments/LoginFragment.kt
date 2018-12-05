package com.example.projectonppo.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectonppo.Managers.Databases.Manager
import com.example.projectonppo.Listeners.SettingsLoader
import com.example.projectonppo.R
import com.example.projectonppo.Validations.ValidationForEmail
import com.example.projectonppo.Validations.ValidationForRequired
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

        //editEmailLogin?.text = SpannableStringBuilder("red@mail.ru")
        //editPasswordLogin?.text = SpannableStringBuilder("123456")

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
                        if (manager.successSign == true)
                            findNavController().navigate(R.id.userFragment)
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
                Toast.makeText(context, resources.getText(R.string.correct_mistakes), Toast.LENGTH_SHORT).show()
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
        if ((editEmailLogin?.error != null) or (editPasswordLogin?.error != null))
            return false
        return true
    }
}


