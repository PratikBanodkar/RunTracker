package com.appedia.runtracker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.FragmentSetingsBinding
import com.appedia.runtracker.util.Constants
import com.appedia.runtracker.util.Constants.KEY_NAME
import com.appedia.runtracker.util.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSetingsBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSetingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataFromPreferences()
        initClickListeners()
    }

    private fun loadDataFromPreferences() {
        val name = sharedPreferences.getString(KEY_NAME, "User")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 80f)
        binding.editTextName.setText(name)
        binding.editTextWeight.setText(weight.toString())
    }

    private fun initClickListeners() {
        binding.buttonSave.setOnClickListener {
            val saveSuccess = saveChangesToSharedPreferences()
            if (saveSuccess)
                Snackbar.make(
                    requireView(),
                    getString(R.string.saved_changes),
                    Snackbar.LENGTH_SHORT
                ).show()
        }
    }

    private fun saveChangesToSharedPreferences(): Boolean {
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
                .putBoolean(Constants.KEY_IS_SETUP_DONE, true)
                .apply()
            requireActivity().findViewById<TextView>(R.id.textViewTitle).text =
                getString(R.string.lets_run_user, name)
            return true
        }
    }

}