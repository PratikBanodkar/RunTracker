package com.appedia.runtracker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.FragmentSetupBinding
import com.appedia.runtracker.util.Constants.KEY_IS_SETUP_DONE
import com.appedia.runtracker.util.Constants.KEY_NAME
import com.appedia.runtracker.util.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isSetupAlreadyDone = false

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

        if (isSetupAlreadyDone) {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.setupFragment, true).build()
            findNavController().navigate(
                R.id.action_setupFragment_to_homeFragment,
                savedInstanceState,
                navOptions
            )
        }

        binding.buttonContinue.setOnClickListener {
            navigateToHomeFragment()
        }
    }

    private fun navigateToHomeFragment() {
        if (findNavController().currentDestination?.id == R.id.setupFragment)
            if (isSetupDone())
                findNavController().navigate(SetupFragmentDirections.actionSetupFragmentToHomeFragment())
    }

    private fun isSetupDone(): Boolean {
        val name = binding.editTextName.text.toString()
        val weight = binding.editTextWeight.text.toString()
        if (name.isEmpty()) {
            binding.editTextName.error = getString(R.string.please_fill_out_this_field)
            return false
        }
        if (weight.isEmpty()) {
            binding.editTextWeight.error = getString(R.string.please_fill_out_this_field)
            return false
        } else {
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight.toFloat())
                .putBoolean(KEY_IS_SETUP_DONE, true)
                .apply()
            requireActivity().findViewById<TextView>(R.id.textViewTitle).text = "Let's run $name !"
            return true
        }
    }
}