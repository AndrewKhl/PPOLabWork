package com.example.projectonppo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import com.example.projectonppo.Manager
import com.example.projectonppo.Models.User
import com.example.projectonppo.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.lang.Exception


class UserFragment: Fragment() {

    private var changeProfileStatus: Boolean = false
    private var currentUser:User? = null
    private var mView: View? = null

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
        val editName: TextView = view!!.findViewById(R.id.editName)
        val editNickname: TextView = view!!.findViewById(R.id.editNickname)
        val editEmail: TextView = view!!.findViewById(R.id.editEmail)
        val editPhone: TextView = view!!.findViewById(R.id.editPhone)

        editName.text = currentUser?.name ?: ""
        editNickname.text = currentUser?.nickname ?: ""
        editEmail.text = currentUser?.email ?: ""
        editPhone.text = currentUser?.phone ?: ""

        var a = editName.text as String
    }

    private fun getUserChange(): User
    {
        val editName: TextView = view!!.findViewById(R.id.editName)
        val editNickname: TextView = view!!.findViewById(R.id.editNickname)
        val editEmail: TextView = view!!.findViewById(R.id.editEmail)
        val editPhone: TextView = view!!.findViewById(R.id.editPhone)

        val name = editName.text.toString().trim()
        val nickname = editNickname.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val phone = editPhone.text.toString().trim()
        return User(name, nickname, email, phone)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bthSave = view.findViewById<Button>(R.id.save_btn)
        val viewSwitcher = view.findViewById<ViewSwitcher>(R.id.switcherEdit)
        changeProfileStatus = false

        val manager = Manager.dataBase
        //var newUser = User("Andrew", "Red", "red@mail.ru", "12331231", "123456")

        manager.signUser("red@mail.ru", "123456")

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

        setUserInfo(manager.getCurrentUser())
    }
}