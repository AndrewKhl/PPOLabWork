package com.example.projectonppo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projectonppo.Fragments.AboutFragment
import com.example.projectonppo.Fragments.Empty1Fragment
import com.example.projectonppo.Fragments.LoginFragment
import com.example.projectonppo.Fragments.UserFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private var refToToggle: ActionBarDrawerToggle? = null
    private var refToDrawer: DrawerLayout? = null
    private var manager = Manager.dataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        manager.signOut()

        val mainLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        refToDrawer = mainLayout
        val toggleOnSideBar = ActionBarDrawerToggle(this, mainLayout, R.string.open, R.string.close)
        refToToggle = toggleOnSideBar

        val navigationView: NavigationView = findViewById(R.id.nav_menu)
        navigationView.setNavigationItemSelectedListener(this)

        mainLayout.addDrawerListener(toggleOnSideBar)
        toggleOnSideBar.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction().replace(R.id.fragments_container, LoginFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_logout)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var dontViewMessage = manager.userInSystem()

        when (item.itemId) {
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
        refToDrawer?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_button){
            supportFragmentManager.beginTransaction().replace(R.id.fragments_container, AboutFragment()).commit()
            return true
        }

        if (refToToggle!!.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.nav_menu)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }
}

