package com.example.projectonppo

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.projectonppo.Databases.Manager
import com.example.projectonppo.Fragments.AboutFragment
import com.example.projectonppo.Fragments.Empty1Fragment
import com.example.projectonppo.Fragments.LoginFragment
import com.example.projectonppo.Fragments.UserFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(){
    private var refToToggle: ActionBarDrawerToggle? = null
    private var refToDrawer: DrawerLayout? = null
    private var manager = Manager.dataBase
    private var drawerLayout: DrawerLayout? = null

    private var ref_menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = host?.findNavController()

        val sideNavView = findViewById<NavigationView>(R.id.nav_menu)
        sideNavView?.setupWithNavController(navController!!)

        drawerLayout = findViewById(R.id.drawerLayout)
        setupActionBarWithNavController(navController!!, drawerLayout)

        /*navController?.addOnNavigatedListener{ _, desctination ->
            val dest: String = try {
                resources.getResourceName(desctination.id)
            }
            catch (e: Resources.NotFoundException) {
                desctination.id.toString()
            }
        }*/


        /*
        val mainLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        refToDrawer = mainLayout

        refToToggle = toggleOnSideBar

        val navigationView: NavigationView = findViewById(R.id.nav_menu)
        //navigationView.setNavigationItemSelectedListener(this)

        mainLayout.addDrawerListener(toggleOnSideBar)
        toggleOnSideBar.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction().replace(R.id.fragments_container, LoginFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_logout)
        }*/*/
    }
    /*
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var dontViewMessage = manager.userInSystem()

        /*when (item.itemId) {
            R.id.nav_account -> {
                if (dontViewMessage)
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_container, UserFragment()).commit()
            }
            R.id.nav_empty1 -> {
                if (dontViewMessage)
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_container, Empty1Fragment()).commit()
            }
            R.id.nav_logout -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragments_container, LoginFragment()).commit()
                dontViewMessage = true
            }
        }

        if (!dontViewMessage)
            Toast.makeText(this, "Please, enter the system", Toast.LENGTH_SHORT).show()
        refToDrawer?.closeDrawer(GravityCompat.START)*/
        return true
    }*/



    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(drawerLayout,
                findNavController( R.id.nav_host_fragment))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*if (item.itemId == R.id.about_button){
            supportFragmentManager.beginTransaction().replace(R.id.fragments_container, AboutFragment()).commit()
            return true
        }

        if (refToToggle!!.onOptionsItemSelected(item))
            return true*/

        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment)) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }
}

