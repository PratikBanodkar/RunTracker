package com.appedia.runtracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.FragmentTrackingBinding
import com.appedia.runtracker.services.ListOfPaths
import com.appedia.runtracker.services.ServiceState
import com.appedia.runtracker.services.TrackingService
import com.appedia.runtracker.ui.viewmodels.MainViewModel
import com.appedia.runtracker.util.*
import com.appedia.runtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_STOP_SERVICE
import com.appedia.runtracker.util.Constants.MAP_ZOOM
import com.appedia.runtracker.util.Constants.POLYLINE_COLOR
import com.appedia.runtracker.util.Constants.POLYLINE_WIDTH
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentTrackingBinding
    private var map: GoogleMap? = null
    private val TAG = TrackingFragment::class.java.simpleName
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentTrackingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            updateButtonsBasedOnServiceState(TrackingService.serviceState.value)
        }
        getGoogleMap()
        initClickListeners()
        observeServiceData()
        setUpBackPressedCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tracking_toolbar, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancelTracking -> showCancelRunConfirmationAlert()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (TrackingService.serviceState.value == ServiceState.RUNNING || TrackingService.serviceState.value == ServiceState.PAUSED)
            this.menu?.getItem(0)?.isVisible = true
    }

    private fun setUpBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun updateButtonsBasedOnServiceState(serviceState: ServiceState?) {
        serviceState?.let {
            when (it) {
                ServiceState.RUNNING -> {
                    showPauseButton()
                    showStopButton()
                    showCancelButton()
                }
                ServiceState.PAUSED -> {
                    showPlayButton()
                    showStopButton()
                    showCancelButton()
                }
                else -> {
                }
            }
        }
    }

    private fun observeServiceData() {
        TrackingService.serviceState.observe(viewLifecycleOwner, { serviceState ->
            updateButtonsBasedOnServiceState(serviceState)
        })

        TrackingService.runPaths.observe(viewLifecycleOwner, { listOfPaths ->
            drawLatestPathFromListOfPaths(listOfPaths)
        })

        TrackingService.runTime.observe(viewLifecycleOwner, { runDuration ->
            if (TrackingService.serviceState.value == ServiceState.RUNNING)
                binding.textViewTimer.text = runDuration
        })
    }

    private fun initClickListeners() {
        binding.buttonPlayPause.setOnClickListener {
            showStopButton()
            startOrResumeTrackingService()

        }
        binding.buttonStop.setOnClickListener {
            finishCurrentRun()
        }
    }

    private fun getGoogleMap() {
        binding.mapView.getMapAsync {
            map = it
            drawAllPathsFromListOfPaths()
        }
    }

    private fun showCancelRunConfirmationAlert() {
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.cancel_run))
            .setIcon(R.drawable.ic_cancel)
            .setMessage(getString(R.string.sure_want_to_cancel))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                cancelCurrentRun()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
        alertDialog.show()
    }

    private fun cancelCurrentRun() {
        sendCommandToTrackingService(ACTION_STOP_SERVICE)
        onBackPressed()
    }

    private fun onBackPressed() {
        findNavController().popBackStack(R.id.homeFragment, false);
    }


    private fun showFinishRunConfirmationAlert() {
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Finish Run")
            .setIcon(R.drawable.ic_run)
            .setMessage(getString(R.string.sure_you_want_to_finish_run_and_save_it))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                finishCurrentRun()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
        alertDialog.show()
    }

    private fun finishCurrentRun() {

    }

    private fun startOrResumeTrackingService() {
        when (TrackingService.serviceState.value) {
            // Service is Running. Then Pause and show Play button
            ServiceState.RUNNING -> {
                sendCommandToTrackingService(ACTION_PAUSE_SERVICE)
                showPlayButton()
            }
            // Service needs to Start for first time or Resume from Paused state. Show pause button
            else -> {
                sendCommandToTrackingService(ACTION_START_OR_RESUME_SERVICE)
                showPauseButton()
            }
        }
    }

    private fun showStopButton() {
        binding.buttonStop.show()
    }

    private fun showPauseButton() {
        binding.buttonPlayPause.icon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_pause, null)
    }

    private fun showPlayButton() {
        binding.buttonPlayPause.icon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_play, null)
    }

    private fun showCancelButton() {
        this.menu?.getItem(0)?.isVisible = true
    }

    private fun sendCommandToTrackingService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun drawLatestPathFromListOfPaths(listOfPaths: ListOfPaths) {
        if (listOfPaths.hasAtleastTwoPoints()) {
            val preLastLatLng = listOfPaths.getSecondLastPoint()
            val lastLatLng = listOfPaths.getLastPoint()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
        updateMapCameraToUserLocation(listOfPaths)
    }

    private fun updateMapCameraToUserLocation(listOfPaths: ListOfPaths) {
        if (listOfPaths.hasAtleastOnePoint()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    listOfPaths.getLastPoint(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun drawAllPathsFromListOfPaths() {
        TrackingService.runPaths.value?.let { listOfPaths ->
            for (path in listOfPaths) {
                val polylineOptions = PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .addAll(path)
                map?.addPolyline(polylineOptions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}