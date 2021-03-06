package com.example.projectonppo.managers

import android.app.Activity
import android.net.Uri
import androidx.navigation.NavController
import com.example.projectonppo.R
import java.lang.Exception

class DeepLinksManager {
    companion object {
        fun addNavigation(navController: NavController, activity: Activity){
            val data : Uri? = activity.intent.data
            val lastPathSegment : String? = data?.lastPathSegment

            var pageNumber = 0
            try {
                if (data?.path?.matches("^/page/\\d*$".toRegex())!!)
                    pageNumber = lastPathSegment!!.toInt()
            }
            catch (e: Exception){}

            when (pageNumber){
                0 -> navController.navigate(R.id.userFragment)
                1 -> navController.navigate(R.id.aboutFragment)
                2 -> navController.navigate(R.id.urlsFragment)
            }
        }
    }
}