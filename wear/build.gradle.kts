plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.glucoseforwatch.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.glucoseforwatch.mobile"
        minSdk = 30
        targetSdk = 36
        versionCode = 25
        versionName = "0.6.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Local ProGuard/Doze validation only — no production keystore yet.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:datalayer-contract"))
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.wear.compose:compose-material3:1.5.6")
    implementation("androidx.wear.compose:compose-foundation:1.5.6")
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    implementation("androidx.wear:wear:1.4.0")
    implementation("androidx.wear.watchface:watchface-complications-data:1.2.1")
    implementation("androidx.wear.watchface:watchface-complications-data-source:1.2.1")

    // Wear Tiles runtime + Studio Design-pane tooling (aligned versions).
    // Docs: https://developer.android.com/training/wearables/tiles/debug
    // tiles-tooling-preview / wear-tooling-preview = implementation (Studio discovery)
    // tiles-tooling = debugImplementation (preview renderer)
    implementation("androidx.wear.tiles:tiles:1.6.1")
    implementation("androidx.wear.tiles:tiles-material:1.6.1")
    implementation("androidx.wear.tiles:tiles-tooling-preview:1.6.1")
    implementation("androidx.wear:wear-tooling-preview:1.0.0")
    debugImplementation("androidx.wear.tiles:tiles-tooling:1.6.1")
    implementation("com.google.guava:guava:33.2.1-android")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.wear.compose:compose-ui-tooling:1.5.6")
}
