plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val gfwVersionCode = providers.gradleProperty("gfwVersionCode").get().toInt()
val gfwVersionName = providers.gradleProperty("gfwVersionName").get()

android {
    namespace = "com.glucoseforwatch.wear"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.glucoseforwatch.mobile"
        minSdk = 30
        targetSdk = 37
        versionCode = gfwVersionCode
        versionName = gfwVersionName
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
        // Generate BuildConfig to allow BuildConfig.DEBUG guards in wear module
        buildConfig = true
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.wear.compose.material3)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.play.services.wearable)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.androidx.wear)
    implementation(libs.androidx.watchface.complications.data)
    implementation(libs.androidx.watchface.complications.data.source)

    // Wear Tiles runtime + Studio Design-pane tooling (aligned versions).
    // Docs: https://developer.android.com/training/wearables/tiles/debug
    // tiles-tooling-preview / wear-tooling-preview = implementation (Studio discovery)
    // tiles-tooling = debugImplementation (preview renderer)
    implementation(libs.androidx.wear.tiles)
    implementation(libs.androidx.wear.tiles.tooling.preview)
    implementation(libs.androidx.wear.tooling.preview)
    debugImplementation(libs.androidx.wear.tiles.tooling)
    implementation(libs.guava)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.core)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.wear.compose.ui.tooling)
}
