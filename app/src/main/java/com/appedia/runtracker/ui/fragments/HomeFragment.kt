package com.appedia.runtracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appedia.runtracker.R
import com.appedia.runtracker.adapters.RunAdapter
import com.appedia.runtracker.databinding.FragmentHomeBinding
import com.appedia.runtracker.ui.viewmodels.MainViewModel
import com.appedia.runtracker.util.PermissionUtils
import com.appedia.runtracker.util.gone
import com.appedia.runtracker.util.show
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var runAdapter: RunAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setupRecyclerView()
        setupFilterSpinner()
        setClickListeners()
        observeViewModelData()
    }

    private fun setupRecyclerView() = binding.recyclerViewRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
        addItemDecoration(RunAdapter.SpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_view_item_spacing)))
    }

    private fun setupFilterSpinner() {
        binding.spinnerFilter.apply {
            spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.filter_options)
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.updateFilterOption(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }
        }
    }

    private fun setClickListeners() {
        binding.fabNewRun.setOnClickListener {
            navigateToTrackingFragment()
        }
    }

    private fun observeViewModelData() {
        viewModel.runs.observe(viewLifecycleOwner, {
            if (it.isEmpty())
                binding.textViewNoRunsSaved.show()
            else {
                binding.textViewNoRunsSaved.gone()
                runAdapter.submitList(it)
            }
        })
    }

    private fun navigateToTrackingFragment() {
        if (findNavController().currentDestination?.id == R.id.homeFragment)
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTrackingFragment())
    }

    private fun requestPermissions() {
        if (PermissionUtils.hasLocationPermission(requireContext())) {
            return
        }
        PermissionUtils.requestLocationPermissions(this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (PermissionUtils.somePermissionDeniedForever(this, perms)) {
            PermissionUtils.showAppSettingsDialog(this)
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionResult(requestCode, permissions, grantResults, this)

    }
}