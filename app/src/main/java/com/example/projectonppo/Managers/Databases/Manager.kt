package com.example.projectonppo.Managers.Databases

import com.example.projectonppo.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.google.android.gms.tasks.OnSuccessListener
import android.app.ProgressDialog
import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import java.io.File
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception


class Manager private constructor() {

    private var mAuth: FirebaseAuth
    private var dbUsers: DatabaseReference
    private var mStorage: StorageReference
    private var currentUser: User? = null
    var successSign: Boolean? = null
    var successRegistration: Boolean? = null

    init {
        dbUsers = FirebaseDatabase.getInstance().getReference("users")
        mStorage = FirebaseStorage.getInstance().getReference("avatars")
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
        mAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task ->
            successRegistration = if (task.isSuccessful) {
                signUser(user.email, user.password)
                user.password = ""
                currentUser = user
                dbUsers.child(mAuth.currentUser!!.uid).setValue(user)
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
                true
            } else
                false
        }
    }

    fun signOut() {
        mAuth.signOut()
        currentUser = null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun userInSystem(): Boolean {
        return mAuth.currentUser != null
    }

    fun changeUser(newUserData: User) {
        if (currentUser?.email != newUserData.email) {
            mAuth.currentUser?.updateEmail(newUserData.email)
        }

        if (currentUser != newUserData) {
            dbUsers.child(mAuth.currentUser!!.uid).setValue(newUserData)
            currentUser = newUserData
        }
    }

    private fun setListenerOnDatabase() {
        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAuth.currentUser != null)
                    currentUser = dataSnapshot.child(mAuth.currentUser!!.uid).getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun uploadImageToFirebaseStorage(data: Uri) {

        mStorage.child(mAuth.currentUser!!.uid + ".jpg").putFile(data)

    }
    /*
    fun downloadFromFirebaseStorage() {
        val pDialog = view.getProgressDialog()
        pDialog.setIndeterminate(false)
        pDialog.setCancelable(false)
        val image_storage = manager.getImageStorage()
        if (image_storage != null) {
            pDialog.setTitle("Downloading...")
            pDialog.setMessage(null)
            pDialog.show()
            try {
                val localFile = File.createTempFile("images", ".jpg")

                image_storage!!.getFile(localFile).addOnSuccessListener(OnSuccessListener<Any> {
                    val bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath())
                    view.setProfileImg(bmp)
                    saveUser()
                    pDialog.dismiss()
                }).addOnFailureListener(OnFailureListener {
                    pDialog.dismiss()
                    view.onErrorMessage("Download failed. Check internet connection")
                }).addOnProgressListener(object : OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    fun onProgress(taskSnapshot: FileDownloadTask.TaskSnapshot) {
                        val progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()

                        pDialog.setMessage("Downloaded " + progress.toInt() + "%...")
                    }
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            view.onErrorMessage("Upload file before downloading")
        }
    }*/
}
