plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.lesadrax.registrationclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lesadrax.registrationclient"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\MorphoKit.jar"))
    implementation(files("libs\\USBManager.jar"))
    implementation(files("libs\\MPManager.jar"))
    implementation(files("libs\\mtopsUsbManager.jar"))
    implementation(files("libs\\mtopsUsbManager.jar"))
    implementation(files("libs\\mtopsUsbManager.jar"))
    implementation(files("libs\\MTOPSPlugUnplug.aar"))
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.sweetAlert)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler) // For Java
    implementation(libs.xmlToPdf) // For Java
    implementation(libs.dexter) // For Java
    implementation(libs.bCrypt) // For Java
    //implementation(libs.firebase_analytics)

}