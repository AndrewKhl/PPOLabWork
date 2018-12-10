package com.example.projectonppo

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1
    private var imeiTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val versionTextView: TextView = findViewById(R.id.ValueVersion)
        versionTextView.text = BuildConfig.VERSION_NAME

        imeiTextView = findViewById(R.id.ValueIMEI)
        val message = MessageView(this)
        message.show()
        getDeviceIMEI()
    }

    private fun getDeviceIMEI(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                val dialog = AlertDialog.Builder(this)
                dialog.setMessage(resources.getString(R.string.permissionText))
                dialog.setTitle(resources.getString(R.string.permissionTitle))
                dialog.setCancelable(false)
                dialog.setPositiveButton("OK") { dialogInterface, which ->
                    requestPermission()
                }
                dialog.show()
            }
            else {
                requestPermission()
            }
        }
        else {
            outputIMEI()
        }
    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PERMISSIONS_REQUEST_READ_PHONE_STATE)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    outputIMEI()
                } else {
                    imeiTextView?.text = resources.getString(R.string.noPermission)
                }
                return
            }
        }
    }

    private fun outputIMEI(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            return
        val telManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        ValueIMEI.text = telManager.deviceId
    }
}
