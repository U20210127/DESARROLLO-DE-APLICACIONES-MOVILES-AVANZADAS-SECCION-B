plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.incidentes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.incidentes"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    implementation(libs.play.services.fitness)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

// Converter GSON (para parsear JSON a objetos Java automáticamente)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.material:material:1.6.0") // Asegúrate de usar una versión compatible de Material Components

    implementation("androidx.drawerlayout:drawerlayout:1.1.1")

    implementation("com.github.bumptech.glide:glide:4.13.0")// Agregar Glide
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")
}