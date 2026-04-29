import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.widgetg7.mobile"
    compileSdk = 36

    defaultConfig {
        //noinspection SpellCheckingInspection
        val dexcomShareApplicationId =
            ((project.findProperty("dexcomShareApplicationId") as? String)
                ?: "d89443d2-327c-4a6f-89e5-496bbb0317db").replace("\"", "\\\"")

        applicationId = "com.widgetg7.mobile"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        buildConfigField("String", "DEXCOM_SHARE_APPLICATION_ID", "\"$dexcomShareApplicationId\"")
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

    buildFeatures {
        buildConfig = true
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
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.security:security-crypto:1.1.0")
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    implementation("androidx.wear:wear-remote-interactions:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.11.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
}
