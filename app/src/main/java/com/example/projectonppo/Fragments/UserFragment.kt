package com.example.projectonppo.Fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectonppo.Listeners.SettingsLoader
import com.example.projectonppo.Managers.Databases.Manager
import com.example.projectonppo.Models.User
import com.example.projectonppo.R
import com.example.projectonppo.Validations.ValidationForEmail
import com.example.projectonppo.Validations.ValidationForPhone
import com.example.projectonppo.Validations.ValidationForRequired
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment: Fragment() {
    private var manager = Manager.dataBase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!manager.userInSystem()){
            Toast.makeText(context, "Please, enter the system", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
            return null
        }
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
        editNameChange?.text = SpannableStringBuilder(currentUser?.name)
        editNicknameChange?.text = SpannableStringBuilder(currentUser?.nickname)
        editEmailChange?.text = SpannableStringBuilder(currentUser?.email)
        editPhoneChange?.text = SpannableStringBuilder(currentUser?.phone)
    }

    private fun getUserChange(): User {
        val name = editNameChange?.text.toString().trim()
        val nickname = editNicknameChange?.text.toString().trim()
        val email = editEmailChange?.text.toString().trim()
        val phone = editPhoneChange?.text.toString().trim()
        return User(name, nickname, email, phone)
    }

    private fun setValidationToEdit(){
        editNicknameChange?.addTextChangedListener(ValidationForRequired(editNicknameChange, "Nickname"))
        editEmailChange?.addTextChangedListener(ValidationForEmail(editEmailChange))
        editPhoneChange?.addTextChangedListener(ValidationForPhone(editPhoneChange))
    }

    private fun checkEditOnError(): Boolean{
        if ((editEmailChange.error != null) || (editEmailChange.text.isEmpty()))
            return false
        if ((editNicknameChange.error != null) || (editNicknameChange.text.isEmpty()))
            return false
        if ((editNameChange.error != null) || (editNameChange.text.isEmpty()))
            return false
        if ((editPhoneChange.error != null) || (editPhoneChange.text.isEmpty()))
            return false
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var changeProfileStatus = false

        setValidationToEdit()

        btnSave.setOnClickListener {
            if (!changeProfileStatus) {
                setUserEdit(manager.getCurrentUser())
                btnSave.text = "Save"
                changeProfileStatus = true
                viewSwitcherChange.showNext()
            }
            else {
                if (checkEditOnError()) {
                    saveChangeUser()
                    btnSave.text = "Change"
                    changeProfileStatus = false
                }
                else
                    Toast.makeText(context, "Correct the mistakes", Toast.LENGTH_SHORT).show()
            }
        }

        avatar.setOnClickListener{
            showSelectChangeDialog()
        }

        waitUserLoad()
    }

    private fun setCurrentUser(){
        if (manager.successDownloadAvatar == true)
            avatar?.setImageBitmap(manager.currentAvatar)
        setUserInfo(manager.getCurrentUser())
        setUserEdit(manager.getCurrentUser())
    }

    private fun waitUserLoad(){
        if (manager.getCurrentUser() != null) {
            setCurrentUser()
            return
        }

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Load user...")
        progressDialog.setCancelable(false)

        SettingsLoader(object : SettingsLoader.LoadListener
        {
            override fun onPreExecute() {
                progressDialog.show()
            }

            override fun onPostExecute() {
                progressDialog.dismiss()
                setCurrentUser()
            }

            override fun doInBackground() {
                while(true){
                    if ((manager.getCurrentUser() != null))
                        break
                }
                manager.downloadAvatarFromDatabase()
                while(true){
                    if (manager.successDownloadAvatar != null)
                        break
                }
            }
        }).execute()
    }

    override fun onDestroyView() {
        saveChangeUser()
        super.onDestroyView()
    }

    private fun saveChangeUser(){
        val newUser = getUserChange()
        val oldUSer = manager.getCurrentUser() ?: return
        if (!newUser.compare(oldUSer)) {
            val dialog = AlertDialog.Builder(context!!)
            dialog.setMessage("Data has been changed, do you want to save it?")
            dialog.setTitle("Warning")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Yes") { _, _ ->
                run {
                    manager.changeUser(newUser)
                    setUserInfo(manager.getCurrentUser())
                    viewSwitcherChange.showNext()
                }
            }

            dialog.setNegativeButton("No") { _, _ ->
                viewSwitcherChange.showNext()
            }

            dialog.show()
        }
        else
            viewSwitcherChange.showNext()
    }

    private val PERMISSIONS_REQUEST_CAMERA = 1
    private val CAMERA_REQUEST_CODE = 2
    private val GALLERY_REQUEST_CODE = 3

    private fun showSelectChangeDialog()  {
        val photoMods = arrayOf("Create photo", "Select photo from gallery")
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Photo")

        builder.setItems(photoMods) { _, which ->
            when (photoMods[which]) {
                "Create photo" -> {
                    if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showPermissionDialog(Manifest.permission.CAMERA, PERMISSIONS_REQUEST_CAMERA, "Permission for the camera is necessary to create a photo")
                    } else {
                        setPhoto()
                    }
                }
                "Select photo from gallery" -> {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    if (galleryIntent.resolveActivity(activity!!.packageManager) != null)
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
                }
            }
        }
        builder.show()
    }

    private fun showPermissionDialog(permission: String, permissionCode: Int,
                                     permissionText: String){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
            val dialog = AlertDialog.Builder(activity!!)
            dialog.setMessage(permissionText)
            dialog.setPositiveButton("OK") { _, _ ->
                requestPermission(permission, permissionCode)
            }
            dialog.show()
        }
        else {
            requestPermission(permission, permissionCode)
        }
    }

    private fun requestPermission(permission: String, permissionCode: Int){
        requestPermissions(arrayOf(permission), permissionCode)
    }

    private fun setPhoto(){
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(activity!!.packageManager) != null)
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (data != null){
                    val bitmap = data.extras!!.get("data") as Bitmap
                    manager.uploadAvatarInDatabase(bitmap)
                    avatar?.setImageBitmap(bitmap)
                }
            }
            GALLERY_REQUEST_CODE -> {
                if (data != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, data.data)
                    manager.uploadAvatarInDatabase(bitmap)
                    avatar?.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setPhoto()
                }
            }
        }
    }

}