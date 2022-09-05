package com.example.curity.MainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.curity.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navC = findNavController(R.id.fragment)

        bottomNavigationView.setupWithNavController(navC)
    }
}