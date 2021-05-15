package com.appedia.runtracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.appedia.runtracker.R
import com.appedia.runtracker.databinding.FragmentTrackingBinding
import com.appedia.runtracker.services.TrackingService
import com.appedia.runtracker.ui.viewmodels.MainViewModel
import com.appedia.runtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.appedia.runtracker.util.Constants.ACTION_STOP_SERVICE
import com.appedia.runtracker.util.show
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentTrackingBinding
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentTrackingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        getGoogleMap()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.buttonPlayPause.setOnClickListener {
            showStopButton()
            startOrResumeTrackingService()

        }
        binding.buttonStop.setOnClickListener {
            stopTrackingService()
        }
    }

    private fun getGoogleMap() {
        binding.mapView.getMapAsync {
            map = it
        }
    }

    private fun stopTrackingService() {
        sendCommandToTrackingService(ACTION_STOP_SERVICE)
    }

    private fun startOrResumeTrackingService() {
        if(TrackingService.serviceState.value == TrackingService.ServiceState.RUNNING) {
            sendCommandToTrackingService(ACTION_PAUSE_SERVICE)
            showPlayButton()
        }
        else {
            sendCommandToTrackingService(ACTION_START_OR_RESUME_SERVICE)
            showPauseButton()
        }
    }

    private fun showStopButton() {
        binding.buttonStop.show()
    }

    private fun showPauseButton(){
        binding.buttonPlayPause.icon = ResourcesCompat.getDrawable(resources,R.drawable.ic_pause,null)
    }

    private fun showPlayButton(){
        binding.buttonPlayPause.icon = ResourcesCompat.getDrawable(resources,R.drawable.ic_play,null)
    }

    private fun sendCommandToTrackingService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

   /* private fun addLatestSinglePolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun updateCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }*/

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