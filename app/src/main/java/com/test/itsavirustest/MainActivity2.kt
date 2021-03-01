package com.test.itsavirustest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.test.itsavirustest.ui.dashboard.MyOrderFragment
import com.test.itsavirustest.ui.home.HomeFragment
import com.test.itsavirustest.ui.notifications.NotificationsFragment

class MainActivity2 : AppCompatActivity() {
    lateinit var toolbar: ActionBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("TAG", "onCreateeeee: ")

        toolbar = supportActionBar!!

        openFragment(HomeFragment())

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    toolbar.title = "Home"
                    val homeFragment = HomeFragment.newInstance()
                    openFragment(homeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_myorder -> {
                    toolbar.title = "Feed"
                    val feedFragment = MyOrderFragment.newInstance()
                    openFragment(feedFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_account -> {
                    toolbar.title = "Cart"
                    val cartFragment = NotificationsFragment.newInstance()
                    openFragment(cartFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}