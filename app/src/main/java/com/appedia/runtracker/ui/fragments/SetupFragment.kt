package com.appedia.runtracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSetupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonContinue.setOnClickListener {
            navigateToHomeFragment()
        }
    }

    private fun navigateToHomeFragment() {
        if (findNavController().currentDestination?.id == R.id.setupFragment)
            findNavController().navigate(SetupFragmentDirections.actionSetupFragmentToHomeFragment())
    }
}