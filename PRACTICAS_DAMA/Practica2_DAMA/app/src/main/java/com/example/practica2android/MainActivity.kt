package com.example.practica2android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottom_navigation_tab_menu)

        // Cargar Home por defecto
        loadFragment(FragmentHome(), "Home")

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(FragmentHome(), "Home")
                R.id.nav_accounts -> loadFragment(AccountFragment(), "Accounts")
                R.id.nav_transactions -> loadFragment(TransactionFragment(), "Transactions")
                R.id.nav_profile -> loadFragment(ProfileFragment(), "Profile")
                R.id.nav_placeholder -> loadFragment(CashFlowFragment(), "Cash Flow")
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_fragment_container, fragment)
            .commit()

        supportActionBar?.title = title
    }
}
