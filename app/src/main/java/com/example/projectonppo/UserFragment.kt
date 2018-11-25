package com.example.projectonppo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment

class UserFragment: Fragment() {

    private var changeProfileStatus: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bthSave = view.findViewById<Button>(R.id.save_btn)
        val viewSwitcher = view.findViewById<ViewSwitcher>(R.id.switcherEdit)
        changeProfileStatus = false

        bthSave.setOnClickListener {
            if (!changeProfileStatus) {
                bthSave.text = "Save"
                changeProfileStatus = true
            }
            else {
                bthSave.text = "Change"
                changeProfileStatus = false
            }
            viewSwitcher.showNext()
        }

        super.onViewCreated(view, savedInstanceState)
    }
}