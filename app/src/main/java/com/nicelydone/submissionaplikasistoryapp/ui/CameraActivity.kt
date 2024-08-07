package com.nicelydone.submissionaplikasistoryapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.nicelydone.submissionaplikasistoryapp.databinding.ActivityCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
   private lateinit var binding: ActivityCameraBinding
   private var imageCapture: ImageCapture? = null
   private lateinit var cameraExecutor: ExecutorService

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityCameraBinding.inflate(layoutInflater)
      setContentView(binding.root)

      cameraExecutor = Executors.newSingleThreadExecutor()

      binding.captureButton.setOnClickListener {
         takePhoto()
      }

      startCamera()
   }

   private fun startCamera() {
      val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

      cameraProviderFuture.addListener({
         val cameraProvider = cameraProviderFuture.get()
         val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
         }

         imageCapture = ImageCapture.Builder().build()

         val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

         try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
               this, cameraSelector, preview, imageCapture)
         } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
         }

      }, ContextCompat.getMainExecutor(this))
   }

   private fun takePhoto() {
      val imageCapture = imageCapture ?: return

      val photoFile = File(
         externalMediaDirs.firstOrNull(),
         SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
      )
      val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

      imageCapture.takePicture(
         outputOptions,
         ContextCompat.getMainExecutor(this),
         object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
               Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
               val savedUri = Uri.fromFile(photoFile)
               Log.d(TAG, "Photo capture succeeded: $savedUri")

               // Compress the image
               val compressedFile = compressImage(photoFile)

               val resultIntent = Intent().apply {
                  putExtra("photoUri", Uri.fromFile(compressedFile).toString())
               }
               setResult(RESULT_OK, resultIntent)
               finish()
            }
         }
      )
   }

   private fun compressImage(file: File): File {
      // Decode the image file into a Bitmap
      val bitmap = BitmapFactory.decodeFile(file.absolutePath)

      // Create a compressed file
      val compressedFile = File.createTempFile("compressed", ".jpg", cacheDir)

      FileOutputStream(compressedFile).use { fos ->
         // Compress the bitmap to JPEG format with 80% quality
         bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
      }

      // Check the file size and resize if needed
      val maxSizeInBytes: Long = 1 * 1024 * 1024 // 1 MB
      if (compressedFile.length() > maxSizeInBytes) {
         val resizedBitmap = resizeBitmap(bitmap, maxSizeInBytes)
         FileOutputStream(compressedFile).use { fos ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
         }
      }

      return compressedFile
   }

   private fun resizeBitmap(bitmap: Bitmap, maxSizeInBytes: Long): Bitmap {
      val ratio = Math.sqrt((maxSizeInBytes.toDouble() / bitmap.byteCount.toDouble()))
      val newWidth = (bitmap.width * ratio).toInt()
      val newHeight = (bitmap.height * ratio).toInt()
      return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
   }

   companion object {
      private const val TAG = "CameraActivity"
   }
}