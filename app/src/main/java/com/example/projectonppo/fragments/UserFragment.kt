package com.example.projectonppo.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.projectonppo.listeners.SettingsLoader
import com.example.projectonppo.LoginActivity
import com.example.projectonppo.managers.databases.Manager
import com.example.projectonppo.models.User
import com.example.projectonppo.R
import com.example.projectonppo.validations.ValidationForEmail
import com.example.projectonppo.validations.ValidationForPhone
import com.example.projectonppo.validations.ValidationForRequired
import com.example.projectonppo.databinding.FragmentUserBinding
import kotlinx.android.synthetic.main.fragment_user.*

// убрать while true
//!инициализация баз данных в отдельной функции

class UserFragment: Fragment() {
    private val PERMISSIONS_REQUEST_CAMERA = 1
    private val CAMERA_REQUEST_CODE = 2
    private val GALLERY_REQUEST_CODE = 3

    private var manager = Manager.dataBase
    private var changeProfileStatus = false
    private var binding: FragmentUserBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!manager.userInSystem()){
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return null
        }

        hiddenKeyboard()
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_user,container , false)
        return binding?.root
    }

    private fun setValidationToEdit(){
        editNicknameChange.addTextChangedListener(ValidationForRequired(editNicknameChange, resources.getText(R.string.nicknameWithoutColons).toString()))
        editEmailChange.addTextChangedListener(ValidationForEmail(editEmailChange))
        editPhoneChange.addTextChangedListener(ValidationForPhone(editPhoneChange))
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

        setValidationToEdit()

        btnSave.setOnClickListener {
            if (!changeProfileStatus) {
                btnSave.text = resources.getText(R.string.save)
                changeProfileStatus = true
                viewSwitcherChange.showNext()
            }
            else {
                if (checkEditOnError()) {
                    btnSave.text = resources.getText(R.string.change)
                    changeProfileStatus = false
                    saveChangeUser()
                }
                else
                    Toast.makeText(context, resources.getText(R.string.correct_mistakes), Toast.LENGTH_SHORT).show()
            }
            hiddenKeyboard()
        }

        avatar.setOnClickListener{
            showSelectChangeAvatarDialog()
        }

        waitUserLoad()
    }

    private fun setCurrentUser(){
        if (manager.successDownloadAvatar == true)
            avatar.setImageBitmap(manager.currentAvatar)
        val currentUser = manager.getCurrentUser()
        binding?.user = User(
                nickname = currentUser?.nickname.toString(),
                name = currentUser?.name.toString(),
                email = currentUser?.email.toString(),
                phone = currentUser?.phone.toString()
        )
    }

    private fun waitUserLoad(){
        if (manager.getCurrentUser() != null) {
            setCurrentUser()
            return
        }

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(resources.getText(R.string.load_user))
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
                while((manager.getCurrentUser() == null) || (manager.successDownloadAvatar == null)){
                    manager.downloadAvatarFromDatabase()
                }
            }
        }).execute()
    }

    override fun onDestroyView() {
        saveChangeUser(true)
        super.onDestroyView()
    }

    private fun hiddenKeyboard(){
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun saveChangeUser(destroyView: Boolean = false){
        val oldDataUser = manager.getCurrentUser() ?: return
        if (!oldDataUser.compare(binding?.user)) {
            val dialog = AlertDialog.Builder(context!!)
            dialog.setMessage(resources.getText(R.string.save_data_message))
            dialog.setTitle(resources.getText(R.string.warning))
            dialog.setCancelable(false)
            dialog.setPositiveButton(resources.getText(R.string.yes)) { _, _ ->
                run {
                    manager.updateDataUser(binding?.user)
                    if (!destroyView){
                        setCurrentUser()
                        viewSwitcherChange.showNext()
                    }
                }
            }

            dialog.setNegativeButton(resources.getText(R.string.no)) { _, _ ->
                if (!destroyView){
                    setCurrentUser()
                    viewSwitcherChange.showNext()
                }
            }

            dialog.show()
        }
        else
            if (!destroyView)
                viewSwitcherChange.showNext()
    }

    private fun showSelectChangeAvatarDialog()  {
        if (changeProfileStatus){
            val photoMods = arrayOf(resources.getText(R.string.create_photo), resources.getText(R.string.select_gallery))
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(resources.getText(R.string.photo))

            builder.setItems(photoMods) { _, which ->
                when (photoMods[which]) {
                    resources.getText(R.string.create_photo) -> {
                        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showPermissionDialog(Manifest.permission.CAMERA, PERMISSIONS_REQUEST_CAMERA, resources.getText(R.string.camera_permission).toString())
                        } else {
                            setAvatar()
                        }
                    }
                    resources.getText(R.string.select_gallery) -> {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        if (galleryIntent.resolveActivity(activity!!.packageManager) != null)
                            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
                    }
                }
            }
            builder.show()
        }
    }

    private fun showPermissionDialog(permission: String, permissionCode: Int,
                                     permissionText: String){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
            val dialog = AlertDialog.Builder(activity!!)
            dialog.setMessage(permissionText)
            dialog.setPositiveButton(resources.getText(R.string.OK)) { _, _ ->
                requestPermissions(arrayOf(permission), permissionCode)
            }
            dialog.show()
        }
        else {
            requestPermissions(arrayOf(permission), permissionCode)
        }
    }

    private fun setAvatar(){
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(activity!!.packageManager) != null)
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (data != null){
                    val bitmap = data.extras?.get("data") as Bitmap
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
                    setAvatar()
                }
            }
        }
    }

}