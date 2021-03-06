package com.example.projectonppo.managers.databases

import com.example.projectonppo.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.File
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.Exception


class Manager private constructor() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var dbUsers: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var mStorage: StorageReference = FirebaseStorage.getInstance().getReference("avatars")
    private var currentUser: User? = null
    var successSign: Boolean? = null
    var successRegistration: Boolean? = null
    var successDownloadAvatar: Boolean? = null
    var currentAvatar: Bitmap? = null

    init {
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
        mAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task ->
            successRegistration = if (task.isSuccessful) {
                signUser(user.email, user.password)
                user.password = ""
                currentUser = user
                successDownloadAvatar = false
                dbUsers.child(mAuth.currentUser?.uid.toString()).setValue(user)
                true
            } else
                false
        }
    }

    fun signUser(email: String, password: String){
        successSign = null
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            successSign = if (task.isSuccessful) {
                setListenerOnDatabase()
                downloadAvatarFromDatabase()
                true
            } else
                false
        }
    }

    fun signOut() {
        mAuth.signOut()
        currentUser = null
        currentAvatar = null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun userInSystem(): Boolean {
        return mAuth.currentUser != null
    }

    fun updateDataUser(newUserData: User?) {
        if (newUserData != null){
            if (currentUser?.email != newUserData.email) {
                mAuth.currentUser?.updateEmail(newUserData.email)
            }

            if (currentUser != newUserData) {
                dbUsers.child(mAuth.currentUser?.uid.toString()).setValue(newUserData)
                currentUser = newUserData
            }
        }
    }

    private fun setListenerOnDatabase() {
        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAuth.currentUser != null)
                    currentUser = dataSnapshot.child(mAuth.currentUser?.uid.toString()).getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun uploadAvatarInDatabase(bitmap: Bitmap) {
        val byteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream)
        mStorage.child(mAuth.currentUser?.uid + ".jpg").putBytes(byteStream.toByteArray())
        currentAvatar = bitmap
    }

    fun downloadAvatarFromDatabase() {
        currentAvatar = null
        successDownloadAvatar = null
        try {
            val localFile = File.createTempFile("avatar", ".jpg")

            mStorage.child(mAuth.currentUser?.uid + ".jpg").getFile(localFile).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentAvatar = BitmapFactory.decodeFile(localFile.absolutePath)
                    successDownloadAvatar = true
                }
                else
                    successDownloadAvatar = false
            }
        }
        catch (e: Exception) {
            currentAvatar = null
            successDownloadAvatar = false
        }
    }
}
