package com.appedia.runtracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.navHostFragment))

        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.statisticsFragment,
                R.id.settingsFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }
}