package com.nicelydone.submissionaplikasistoryapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityUploadBinding
import com.nicelydone.submissionaplikasistoryapp.viewmodel.StoryListViewModel
import com.nicelydone.submissionaplikasistoryapp.viewmodel.UploadState
import com.nicelydone.submissionaplikasistoryapp.viewmodel.UploadViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {
   private lateinit var binding: ActivityUploadBinding
   private var currentImageUri: Uri? = null
   private val viewModel: UploadViewModel by viewModels()

   private var currentLocation: Location? = null

   private val locationRequest by lazy {
      LocationRequest.create().apply {
         priority = LocationRequest.PRIORITY_HIGH_ACCURACY
         interval = 1000
      }
   }

   private val fusedLocationProviderClient by lazy {
      LocationServices.getFusedLocationProviderClient(this)
   }

   private val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
         super.onLocationResult(locationResult)
         currentLocation = locationResult.lastLocation
         fusedLocationProviderClient.removeLocationUpdates(this)
      }
   }

   private val cameraActivityResultLauncher = registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
   ) { result: ActivityResult ->
      if (result.resultCode == RESULT_OK) {
         val uriString = result.data?.getStringExtra("photoUri")
         uriString?.let {
            currentImageUri = Uri.parse(it)
            showImage()
         }
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityUploadBinding.inflate(layoutInflater)
      setContentView(binding.root)

      binding.checkboxLocation.setOnCheckedChangeListener { _, isChecked ->
         if (isChecked) {
            checkLocationPermission()
         } else {
            currentLocation = null
         }
      }

      binding.btnGallery.setOnClickListener {
         checkAndRequestPermissions()
      }

      binding.btnCamera.setOnClickListener {
         checkAndRequestCameraPermission()
      }

      binding.btnUpload.setOnClickListener {
         val description = binding.etDescription.text.toString()
         val lat = currentLocation?.latitude
         val lon = currentLocation?.longitude
         viewModel.uploadImage(description, currentImageUri, lat, lon, this)
      }

      lifecycleScope.launch {
         viewModel.uploadState.collect { state ->
            when(state) {
               UploadState.Idle -> {
                  // Do nothing
               }

               UploadState.Loading -> {
                  binding.loadingAnimation.visibility = View.VISIBLE
                  binding.btnCamera.isEnabled = false
                  binding.btnGallery.isEnabled = false
                  binding.btnUpload.isEnabled = false
                  binding.etDescription.isEnabled = false
               }

               is UploadState.Success -> {
                  showLoadingState(true)
                  binding.loadingAnimation.visibility = View.GONE
                  Toast.makeText(this@UploadActivity, "Upload successful", Toast.LENGTH_SHORT).show()
                  val intent = Intent(this@UploadActivity, MainActivity::class.java).apply {
                     addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                  }
                  setResult(RESULT_OK)
                  startActivity(intent)
               }

               is UploadState.Error -> {
                  showLoadingState(false)
                  binding.loadingAnimation.visibility = View.GONE
                  Toast.makeText(this@UploadActivity, state.message, Toast.LENGTH_SHORT).show()
               }
            }
         }
      }
   }

   private fun showLoadingState(isLoading: Boolean) {
      binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
      binding.btnCamera.isEnabled = !isLoading
      binding.btnGallery.isEnabled = !isLoading
      binding.btnUpload.isEnabled = !isLoading
      binding.etDescription.isEnabled = !isLoading
   }

   private fun checkLocationPermission() {
      if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
         ) != PackageManager.PERMISSION_GRANTED
      ) {
         ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_LOCATION_PERMISSION
         )
      } else {
         getCurrentLocation()
      }
   }

   private fun getCurrentLocation() {
      try {
         fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
         )
      } catch (e: SecurityException) {
         e.printStackTrace()
         Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
      }
   }

   private fun checkAndRequestPermissions() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         val permissionsToRequest = mutableListOf<String>()
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
         }
         if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
         } else {
            startGallery()
         }
      } else {
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
         } else {
            startGallery()
         }
      }
   }

   private fun checkAndRequestCameraPermission() {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
      } else {
         val intent = Intent(this, CameraActivity::class.java)
         cameraActivityResultLauncher.launch(intent)
      }
   }

   private fun startGallery() {
      launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
   }

   private val launcherGallery = registerForActivityResult(
      ActivityResultContracts.PickVisualMedia()
   ) { uri: Uri? ->
      if (uri != null) {
         currentImageUri = uri
         showImage()
      } else {
         Log.d("Photo Picker", "No media selected")
      }
   }

   private fun showImage() {
      currentImageUri?.let {
         Log.d("Image URI", "showImage: $it")
         binding.ivPreviewImage.setImageURI(it)
      }
   }

   override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<String>,
      grantResults: IntArray
   ) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      when (requestCode) {
         REQUEST_CODE_LOCATION_PERMISSION -> {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
               getCurrentLocation()
            } else {
               Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            }
         }
         REQUEST_CODE_PERMISSIONS -> {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
               startGallery()
            } else {
               Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
         }
         REQUEST_CODE_CAMERA_PERMISSION -> {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
               val intent = Intent(this, CameraActivity::class.java)
               cameraActivityResultLauncher.launch(intent)
            } else {
               Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
         }
      }
   }

   companion object {
      private const val TAG = "UploadActivity"
      private const val REQUEST_CODE_PERMISSIONS = 1001
      private const val REQUEST_CODE_CAMERA_PERMISSION = 1002
      private const val REQUEST_CODE_LOCATION_PERMISSION = 1003
   }
}