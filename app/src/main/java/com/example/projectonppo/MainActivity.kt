package com.example.projectonppo

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(){
    private var drawerLayout: DrawerLayout? = null
    //private var manager = Manager.dataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = host?.findNavController()

        val sideNavView = findViewById<NavigationView>(R.id.nav_menu)
        sideNavView?.setupWithNavController(navController!!)

        drawerLayout = findViewById(R.id.drawerLayout)
        setupActionBarWithNavController(navController!!, drawerLayout)

        //adb shell am start -W -a android.intent.action.VIEW -d "sdapp://by.myapp/page/2"
        //DeepLinksManager.addNavigation(navController, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(drawerLayout, findNavController( R.id.nav_host_fragment))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //nicknameInMenu?.text = manager.getCurrentUser()?.nickname
        //emailInMenu?.text = manager.getCurrentUser()?.email
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment)) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }
}

