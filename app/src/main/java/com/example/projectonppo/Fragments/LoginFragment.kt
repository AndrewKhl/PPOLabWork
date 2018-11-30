package com.example.projectonppo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.projectonppo.Manager
import com.example.projectonppo.R
import com.example.projectonppo.Validations.ValidationForEmail

class LoginFragment: Fragment() {

    private var editEmail: EditText? = null
    private var editPassword: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = Manager.dataBase
        editEmail = view.findViewById(R.id.loginEmail)
        editPassword = view.findViewById(R.id.loginPassword)

        editEmail?.addTextChangedListener(ValidationForEmail(editEmail))

        val bthLogin = view.findViewById<Button>(R.id.bthLogin)

        bthLogin.setOnClickListener {
            val email = editEmail?.text.toString().trim()
            val password = editPassword?.text.toString().trim()
            manager.signUser(email, password)
            if (manager.correctDataUser == true){
                fragmentManager!!.beginTransaction().replace(R.id.fragments_container, UserFragment()).commit()
            }

        }
    }

}