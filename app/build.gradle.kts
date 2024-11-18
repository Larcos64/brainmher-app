plugins {
    id("com.android.application")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.uan.brainmher"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.uan.brainmher"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    //implementation("com.google.android.material:material:1.10.0")
    //implementation("com.google.android.material:material:1.13.0-alpha05") // Material Design
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Dependencies for views
    //implementation("com.google.android.material:material:1.1.0-alpha10")
    implementation("de.hdodenhof:circleimageview:3.1.0") // CircleImage
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    //implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")

    // OneSignal
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")

    implementation("com.google.code.gson:gson:2.11.0")

    // Login with Google
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // dependence for create of Gif
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.29")

    // ProfileInstaller: Optimize application performance
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")

    // HTTP Requests for OneSignal petitions
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}