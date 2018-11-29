package com.example.projectonppo.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.projectonppo.BuildConfig
import com.example.projectonppo.R

class AboutFragment: Fragment() {
    private val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1
    private var imeiTextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val versionTextView: TextView? = view.findViewById(R.id.ValueVersion)
        versionTextView?.text = BuildConfig.VERSION_NAME

        imeiTextView = view.findViewById(R.id.ValueIMEI)
        getDeviceIMEI()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getDeviceIMEI(){
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.READ_PHONE_STATE)) {
                val dialog = AlertDialog.Builder(activity!!.applicationContext)
                dialog.setMessage(resources.getString(R.string.permissionText))
                dialog.setTitle(resources.getString(R.string.permissionTitle))
                dialog.setCancelable(false)
                dialog.setPositiveButton("OK") {
                    _, _ -> requestPermission()
                }
                dialog.show()
            }
            else
                requestPermission()
        }
        else
            outputIMEI()
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
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    outputIMEI()
                }
                else
                    imeiTextView?.text = resources.getString(R.string.noPermission)

                return
            }
        }
    }

    private fun outputIMEI(){
        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return
        val telManager = activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imeiTextView?.text = telManager.deviceId
    }
}