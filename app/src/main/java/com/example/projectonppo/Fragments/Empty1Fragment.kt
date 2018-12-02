package com.example.projectonppo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectonppo.Databases.Manager
import com.example.projectonppo.R

class Empty1Fragment: Fragment() {
    private val manager = Manager.dataBase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!manager.userInSystem()){
            Toast.makeText(context, "Please, enter the system", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
            return null
        }
        return inflater.inflate(R.layout.fragment_empty1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}