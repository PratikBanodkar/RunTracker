package com.appedia.runtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.ActivityMainBinding
import com.appedia.runtracker.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navigateToTrackingIfNeeded(intent)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingIfNeeded(intent)
    }

    private fun navigateToTrackingIfNeeded(intent:Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT ){
            findNavController(R.id.navHostFragment).navigate(R.id.global_action_to_tracking_fragment)
        }
    }
}