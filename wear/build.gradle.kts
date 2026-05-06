import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.widgetg7.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.widgetg7.mobile"
        minSdk = 30
        targetSdk = 36
        versionCode = 2
        versionName = "0.3.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:datalayer-contract"))
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    implementation("androidx.wear:wear:1.3.0")
    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.1")

    implementation("androidx.wear.tiles:tiles:1.5.0")
    implementation("androidx.wear.tiles:tiles-material:1.5.0")
    implementation("com.google.guava:guava:33.2.1-android")
}
