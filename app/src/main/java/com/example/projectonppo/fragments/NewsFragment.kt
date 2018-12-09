package com.example.projectonppo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projectonppo.LoginActivity
import com.example.projectonppo.managers.databases.Manager
import com.example.projectonppo.R

class NewsFragment: Fragment() {
    private val manager = Manager.dataBase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!manager.userInSystem()){
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return null
        }
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}