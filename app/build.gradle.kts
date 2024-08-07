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
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }
   kotlinOptions {
      jvmTarget = "1.8"
   }
}

dependencies {
   val camerax_version = "1.4.0-beta02"
   implementation("androidx.camera:camera-core:${camerax_version}")
   implementation("androidx.camera:camera-camera2:${camerax_version}")
   implementation("androidx.camera:camera-lifecycle:$camerax_version")
   implementation("androidx.camera:camera-view:$camerax_version")
   implementation("androidx.camera:camera-extensions:$camerax_version")

   implementation("com.google.dagger:hilt-android:2.48")
   kapt("com.google.dagger:hilt-android-compiler:2.48")
   implementation("androidx.activity:activity-ktx:1.9.1")

   implementation("com.github.bumptech.glide:glide:4.16.0")
   implementation("de.hdodenhof:circleimageview:3.1.0")

   implementation("com.squareup.retrofit2:retrofit:2.11.0")
   implementation("com.squareup.retrofit2:converter-gson:2.11.0")
   implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

   implementation("com.airbnb.android:lottie:6.4.1")

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