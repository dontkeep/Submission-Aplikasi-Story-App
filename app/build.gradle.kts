plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.jetbrains.kotlin.android)
   id("kotlin-kapt")
   id("com.google.dagger.hilt.android")
   id("kotlin-parcelize")
}

android {
   namespace = "com.nicelydone.submissionaplikasistoryapp"
   compileSdk = 34

   defaultConfig {
      applicationId = "com.nicelydone.submissionaplikasistoryapp"
      minSdk = 24
      targetSdk = 34
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   buildFeatures {
      viewBinding = true
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
         )
      }
   }

   testOptions {
      unitTests.isReturnDefaultValues = true
   }

   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }
   kotlinOptions {
      jvmTarget = "1.8"
   }
}

dependencies {
   implementation(libs.androidx.room.ktx)
   implementation(libs.play.services.location)

   implementation(libs.androidx.camera.core)
   implementation(libs.androidx.camera.camera2)
   implementation(libs.androidx.camera.lifecycle)
   implementation(libs.androidx.camera.view)
   implementation(libs.androidx.camera.extensions)

   implementation(libs.hilt.android)
   kapt(libs.hilt.android.compiler)
   implementation(libs.androidx.activity.ktx)

   implementation(libs.glide)
   implementation(libs.circleimageview)

   implementation(libs.retrofit)
   implementation(libs.converter.gson)
   implementation(libs.logging.interceptor)

   implementation(libs.lottie)

   implementation(libs.androidx.room.runtime)
   kapt(libs.androidx.room.compiler)
   implementation(libs.androidx.room.paging)

   implementation(libs.androidx.paging.runtime.ktx)

   implementation(libs.play.services.maps)

   testImplementation(libs.androidx.paging.testing)
   testImplementation(libs.junit)

   testImplementation(libs.androidx.core)

   androidTestImplementation(libs.androidx.core.testing)
   androidTestImplementation(libs.kotlinx.coroutines.test)
   testImplementation(libs.androidx.core.testing)
   testImplementation(libs.kotlinx.coroutines.test)
   testImplementation(libs.mockito.core)
   testImplementation(libs.mockito.inline)
   testImplementation(libs.androidx.paging.common.ktx)
   testImplementation(libs.androidx.core.testing.v220)
   implementation(libs.androidx.lifecycle.livedata.ktx)

   implementation(libs.androidx.security.crypto.ktx.v110alpha06)
   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.appcompat)
   implementation(libs.material)
   implementation(libs.androidx.activity)
   implementation(libs.androidx.constraintlayout)
   implementation(libs.androidx.security.crypto.ktx)
   testImplementation(libs.junit)
   androidTestImplementation(libs.androidx.junit)
   androidTestImplementation(libs.androidx.espresso.core)
}