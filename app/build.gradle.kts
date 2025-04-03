plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.iotbazar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.iotbazar"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17  // 🔹 Use Java 17 (Recommended for SDK 35)
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"  // 🔹 Match with Java 17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Material 3
    implementation("androidx.compose.material3:material3:1.2.1")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.3")

    // Debugging
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.3")
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("io.coil-kt:coil-compose:2.2.2") // For image loading
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.google.firebase:firebase-storage-ktx")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.31.3-beta")
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation ("androidx.compose.material3:material3:1.2.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.24.13-rc")

}


