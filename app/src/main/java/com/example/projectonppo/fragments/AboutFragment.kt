package com.example.projectonppo.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.projectonppo.BuildConfig
import com.example.projectonppo.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment: Fragment() {
    private val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        versionTextView?.text = BuildConfig.VERSION_NAME
        getDeviceIMEI()
    }

    private fun getDeviceIMEI(){
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.READ_PHONE_STATE)) {
                val dialog = AlertDialog.Builder(context!!)
                dialog.setMessage(resources.getString(R.string.permissionText))
                dialog.setTitle(resources.getString(R.string.permissionTitle))
                dialog.setCancelable(false)
                dialog.setPositiveButton(resources.getText(R.string.OK)) {
                    _, _ -> requestPermission()
                }
                dialog.show()
            }
            else
                requestPermission()
        }
        else
            outputDeviceIMEI()
    }

    private fun outputDeviceIMEI(){
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return
        val telManager = activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imeiTextView?.text = telManager.deviceId
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PERMISSIONS_REQUEST_READ_PHONE_STATE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    outputDeviceIMEI()
                else
                    imeiTextView?.text = resources.getString(R.string.noPermission)
                return
            }
        }
    }
}