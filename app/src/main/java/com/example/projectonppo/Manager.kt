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
import com.example.projectonppo.R.string.email
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.internal.FirebaseAppHelper.getUid

class Manager private constructor(){

    private var mAuth: FirebaseAuth? = null

    private var dbUsers: DatabaseReference
    private var currentUser: User? = null

    init {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        dbUsers = database.getReference("users")
        mAuth = FirebaseAuth.getInstance()

        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAuth?.currentUser != null)
                    currentUser = dataSnapshot.child(mAuth?.currentUser!!.uid).getValue(User::class.java)
                else
                    currentUser = null
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun registerUser(user: User){
        mAuth?.createUserWithEmailAndPassword(user.email, user.password)
        signUser(user.email, user.password)
        dbUsers.child(mAuth?.currentUser!!.uid).setValue(user)
    }

    fun signUser(email:String, password:String):Boolean {
        return try{
            mAuth?.signInWithEmailAndPassword(email, password)
            true
        }
        catch (e: Exception){
            false
        }
    }

    fun signOut(){
        mAuth?.signOut()
    }

    fun getCurrentUser() :User? {
        return currentUser
    }

    fun userInSystem(): Boolean {
        return mAuth!!.currentUser != null
    }

    fun changeUser(newUserData: User){
        if (currentUser?.email != newUserData.email){
            mAuth?.currentUser?.updateEmail(newUserData.email)
        }

        if (currentUser != newUserData){
            dbUsers.child(mAuth?.currentUser!!.uid).setValue(newUserData)
            currentUser = newUserData
        }
    }

    private object Holder { val singleton = Manager() }

    companion object {
        val dataBase: Manager by lazy { Holder.singleton }
    }
}
