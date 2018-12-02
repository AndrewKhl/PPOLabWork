package com.example.projectonppo.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projectonppo.Listeners.SettingsLoader
import com.example.projectonppo.Manager
import com.example.projectonppo.R
import com.example.projectonppo.Validations.ValidationForEmail
import com.example.projectonppo.Validations.ValidationForRequired
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: Fragment() {

    private var editEmail: EditText? = null
    private var editPassword: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = Manager.dataBase
        manager.signOut()
        editEmail = view.findViewById(R.id.loginEmail)
        editPassword = view.findViewById(R.id.loginPassword)

        setValidationToEdit()

        editEmail?.text = SpannableStringBuilder("red@mail.ru")
        editPassword?.text = SpannableStringBuilder("123456")

        //val bthLogin = view.findViewById<Button>(R.id.bthLogin)
        bthLogin.setOnClickListener {
            if (checkEditOnError()){
                val email = editEmail?.text.toString().trim()
                val password = editPassword?.text.toString().trim()

                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("Check data user...")
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
                            fragmentManager!!.beginTransaction().replace(R.id.fragments_container, UserFragment()).commit()
                        else
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }

                    override fun doInBackground() {
                        while(true){
                            if (manager.successSign == false)
                                break

                            if ((manager.successSign == true) and (manager.getCurrentUser() != null))
                                break
                        }
                    }
                }).execute()
            }
            else
                Toast.makeText(context, "Correct the mistakes", Toast.LENGTH_SHORT).show()
        }

        btnRegistration.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragments_container, RegistrationFragment()).commit()
        }
    }

    private fun setValidationToEdit(){
        editEmail?.addTextChangedListener(ValidationForEmail(editEmail))
        editPassword?.addTextChangedListener(ValidationForRequired(editPassword, "Password"))
    }

    private fun checkEditOnError(): Boolean{
        if ((editEmail?.error != null) or (editPassword?.error != null))
            return false
        return true
    }
}


