
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.ispoke.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.ispoke.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation(libs.androidx.core)
    implementation("androidx.core:core-splashscreen:1.0.1")
    debugImplementation(libs.compose.ui.tooling)
    implementation ("org.tensorflow:tensorflow-lite:2.11.0")
    implementation (libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.tensorflow.lite.task.vision)
    implementation (libs.tensorflow.lite.metadata.v044)
    implementation(libs.coil.compose)
}