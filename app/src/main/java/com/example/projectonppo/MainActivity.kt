package com.example.projectonppo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        outputIMEI()
    }

    fun outputIMEI(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            return
        val telManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val imeiTextView:TextView = findViewById(R.id.ValueIMEI)
        imeiTextView.text = telManager.deviceId
    }
}
