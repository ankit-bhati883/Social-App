package com.example.socialapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        val homeFragment = homeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainframelayout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val homeFragment = homeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainframelayout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.searchpost -> {
                    val searchFragment = SearchFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainframelayout, searchFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.addpost -> {
                    val createPostActivity = createpostFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainframelayout, createPostActivity)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.profile -> {


                    val profileFragment = profileFragment()

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainframelayout, profileFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }
    }

}
