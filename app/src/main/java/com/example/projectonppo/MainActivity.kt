package com.example.projectonppo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projectonppo.Fragments.AboutFragment
import com.example.projectonppo.Fragments.Empty1Fragment
import com.example.projectonppo.Fragments.Empty2Fragment
import com.example.projectonppo.Fragments.UserFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private var refToToggle: ActionBarDrawerToggle? = null
    private var refToDrawer: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        refToDrawer = mainLayout
        val toggleOnSideBar = ActionBarDrawerToggle(this, mainLayout, R.string.open, R.string.close)
        refToToggle = toggleOnSideBar

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        mainLayout.addDrawerListener(toggleOnSideBar)
        toggleOnSideBar.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragments_container, UserFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_account)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_account -> supportFragmentManager.beginTransaction().replace(R.id.fragments_container, UserFragment()).commit()
            R.id.nav_empty1 -> supportFragmentManager.beginTransaction().replace(R.id.fragments_container, Empty1Fragment()).commit()
            R.id.nav_empty2 -> supportFragmentManager.beginTransaction().replace(R.id.fragments_container, Empty2Fragment()).commit()
        }

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
}

