package com.example.kindle.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.kindle.*
import com.example.kindle.fragment.FavouriteFragment
import com.example.kindle.fragment.ProfileFragment
import com.example.kindle.fragment.dashboard_fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout : DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var  toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView : NavigationView
    var previousmenuitem : MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        val sh : SharedPreferences?=getSharedPreferences("user", MODE_PRIVATE)
        val name = sh?.getString("username","").toString()
        navigationView = findViewById(R.id.navigationView)
        val hview=navigationView.getHeaderView(0)
        val log=loginStatus()
        val txtProfileName = hview.findViewById<TextView>(R.id.username)
        txtProfileName.text = name
        setUpToolbar()
        openDashboard()
         val actionBarDrawerToggle = ActionBarDrawerToggle(
                this@MainActivity, drawerLayout,
                R.string.open_drawer, R.string.close_drawer
            )
            //HAMBURGER ICON FUNCTIONING
            drawerLayout.addDrawerListener(actionBarDrawerToggle)
            actionBarDrawerToggle.syncState()
            actionBarDrawerToggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        navigationView.setNavigationItemSelectedListener {
            if(previousmenuitem != null)
            {
                previousmenuitem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousmenuitem=it
            when(it.itemId){
                R.id.dashboard -> {

                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.cart ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,  FavouriteFragment()).addToBackStack("Favourite").commit()
                supportActionBar?.title="Favourite"


                drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, ProfileFragment()).addToBackStack("Profile").commit()
                supportActionBar?.title="Profile"
                drawerLayout.closeDrawers()
                }
                R.id.aboutapp ->{
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("About App")
                    dialog.setMessage("BookHub is an online book store where you can find your favourite books and see their ratings as well as content and buy it out. ")
                    dialog.create()
                    dialog.show()

                drawerLayout.closeDrawers()
                }
                R.id.logout ->{
                    val dialogg = AlertDialog.Builder(this@MainActivity)
                    dialogg.setTitle("Logout")
                    dialogg.setMessage("Are you sure you want to logout? ")
                        .setPositiveButton("LOGOUT",DialogInterface.OnClickListener()  {
                                dialog, id -> finish()
                                log.setLoggedIn(this,false)
                                startActivity(Intent(this,LoginActivity::class.java))
                                  finish()
                        })
                        .setNegativeButton("CANCEL", DialogInterface.OnClickListener()  {
                                dialog, id -> dialog.cancel()
                        })
                    dialogg.create()
                    dialogg.show()

                drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        //HAMBURGER BUTTON
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }
    fun openDashboard(){
         val fragment= dashboard_fragment()
         val transaction = supportFragmentManager.beginTransaction()
         transaction.replace(R.id.frame,fragment)
         transaction.commit()
         supportActionBar?.title="Dashboard"

         navigationView.setCheckedItem(R.id.dashboard)
     }

    override fun onBackPressed() {
        val frag= supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is dashboard_fragment ->openDashboard()
            else-> super.onBackPressed()
        }
    }
}