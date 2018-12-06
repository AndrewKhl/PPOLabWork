package com.example.projectonppo

import android.content.Context
import android.widget.Toast

class MessageView constructor(var context: Context){
    fun show(){
        Toast.makeText(context, "It's developer flavors", Toast.LENGTH_SHORT).show()
    }
}