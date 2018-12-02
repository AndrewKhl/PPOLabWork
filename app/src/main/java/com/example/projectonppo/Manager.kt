package com.example.projectonppo

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.example.projectonppo.Models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import java.lang.Exception
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import android.R.attr.password
import android.app.Activity
import com.example.projectonppo.Fragments.UserFragment
import com.example.projectonppo.Listeners.SettingsLoader
import com.example.projectonppo.R.string.email
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.internal.FirebaseAppHelper.getUid
import java.lang.reflect.Executable

class Manager private constructor() {

    private var mAuth: FirebaseAuth? = null
    private var dbUsers: DatabaseReference
    private var currentUser: User? = null
    var successSign: Boolean? = null
    var successRegistration: Boolean? = null

    init {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        dbUsers = database.getReference("users")
        mAuth = FirebaseAuth.getInstance()
    }

    fun registerUser(user: User) {
        successRegistration = null
        mAuth?.createUserWithEmailAndPassword(user.email, user.password)?.addOnCompleteListener { task ->
            successRegistration = if (task.isSuccessful) {
                setListenerOnDatabase()
                dbUsers.child(mAuth?.currentUser!!.uid).setValue(user)
                user.password = ""
                currentUser = user
                signUser(user.email, user.password)
                true
            } else {
                false
            }
        }
    }

    fun signUser(email: String, password: String){
        successSign = null
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            successSign = if (task.isSuccessful) {
                setListenerOnDatabase()
                true
            } else {
                false
            }
        }
    }

    fun signOut() {
        mAuth?.signOut()
        currentUser = null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun userInSystem(): Boolean {
        return mAuth?.currentUser != null
    }

    fun changeUser(newUserData: User) {
        if (currentUser?.email != newUserData.email) {
            mAuth?.currentUser?.updateEmail(newUserData.email)
        }

        if (currentUser != newUserData) {
            dbUsers.child(mAuth?.currentUser!!.uid).setValue(newUserData)
            currentUser = newUserData
        }
    }

    private object Holder {
        val singleton = Manager()
    }

    companion object {
        val dataBase: Manager by lazy { Holder.singleton }
    }

    private fun setListenerOnDatabase() {
        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAuth?.currentUser != null)
                    currentUser = dataSnapshot.child(mAuth?.currentUser!!.uid).getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
