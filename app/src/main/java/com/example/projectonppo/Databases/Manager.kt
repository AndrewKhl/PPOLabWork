package com.example.projectonppo.Databases

import com.example.projectonppo.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

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
        setListenerOnDatabase()
    }

    private object Holder {
        val singleton = Manager()
    }

    companion object {
        val dataBase: Manager by lazy { Holder.singleton }
    }

    fun registerUser(user: User) {
        successRegistration = null
        mAuth?.createUserWithEmailAndPassword(user.email, user.password)?.addOnCompleteListener { task ->
            successRegistration = if (task.isSuccessful) {
                signUser(user.email, user.password)
                user.password = ""
                currentUser = user
                dbUsers.child(mAuth?.currentUser!!.uid).setValue(user)
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
