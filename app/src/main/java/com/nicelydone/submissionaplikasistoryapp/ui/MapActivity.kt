package com.nicelydone.submissionaplikasistoryapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.nicelydone.submissionaplikasistoryapp.R
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityMapBinding
import com.nicelydone.submissionaplikasistoryapp.viewmodel.StoryListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
   private lateinit var map: GoogleMap
   private lateinit var binding: ActivityMapBinding
   private lateinit var fusedLocationClient: FusedLocationProviderClient
   private val viewModel: StoryListViewModel by viewModels()

   private val locationPermissionRequest: ActivityResultLauncher<Array<String>> = registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
   ) { permissions ->
      if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
         if (::map.isInitialized) { // Check if map is initialized
            enableMyLocation()
         }
      } else {
         Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMapBinding.inflate(layoutInflater)
      setContentView(binding.root)

      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

      val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
      mapFragment.getMapAsync(this)
   }

   override fun onMapReady(googleMap: GoogleMap) {
      map = googleMap
      setCustomMapStyle()
      checkLocationPermission()
      observeStories()
   }

   private fun setCustomMapStyle() {
      try {
         val success = map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
         )
         if (!success) {
            Log.e("MapActivity", "Style parsing failed.")
         }
      } catch (e: Resources.NotFoundException) {
         Log.e("MapActivity", "Can't find style. Error: ", e)
      }
   }

   @SuppressLint("DefaultLocale")
   private fun observeStories() {
      viewModel.storyWithLocation.observe(this) { stories ->
         map.clear()
         stories?.forEach { story ->
            if (story != null) {
               story.lat?.let { lat ->
                  story.lon?.let { lon ->
                     val roundedLat = String.format("%.7f", lat).toDouble()
                     val roundedLon = String.format("%.7f", lon).toDouble()
                     val location = LatLng(roundedLat, roundedLon)
                     map.addMarker(
                        MarkerOptions()
                           .position(location)
                           .title(story.name ?: "Story Location")
                           .snippet(story.description)
                     )
                  }
               }
            }
         }
      }
   }

   private fun checkLocationPermission() {
      when {
         ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
            enableMyLocation()
         }
         else -> {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
         }
      }
   }

   private fun enableMyLocation() {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         map.isMyLocationEnabled = true
         fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
               val currentLatLng = LatLng(it.latitude, it.longitude)
               map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } ?: run {
               Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
            }
         }
      }
   }
}