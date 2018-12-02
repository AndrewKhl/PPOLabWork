package com.example.projectonppo.DeepLinks

import android.app.Activity
import android.net.Uri
import androidx.navigation.NavController
import com.example.projectonppo.R
import java.lang.Exception

/*class DeepLinksManager {
    companion object {
        fun uriNavigate(navController: NavController, activity: Activity){
            val data : Uri? = activity.intent.data
            val lastPathSegment : String? = data?.lastPathSegment
            val pattern = "^/page/\\d*$".toRegex()

            var pageNumber = 0
            try {
                if (data?.path?.matches(pattern)!!)
                    pageNumber = lastPathSegment!!.toInt()
            }
            catch (e: Exception){}

            when (pageNumber){
                0 -> navController.navigate(R.layout.fragment_)
                1 -> navController.navigate(R.id.emptyFragment)
                2 -> navController.navigate(R.id.empty1Fragment)
                3 -> navController.navigate(R.id.aboutFragment)
            }
        }
    }
}*/