plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.widgetg7.mobile"
    compileSdk = 35

    defaultConfig {
        val relayBaseUrl =
            ((project.findProperty("relayBaseUrl") as? String) ?: "https://example.com").replace("\"", "\\\"")
        val relayBearerToken =
            ((project.findProperty("relayBearerToken") as? String) ?: "").replace("\"", "\\\"")
        val dexcomShareApplicationId =
            ((project.findProperty("dexcomShareApplicationId") as? String)
                ?: "d89443d2-327c-4a6f-89e5-496bbb0317db").replace("\"", "\\\"")

        applicationId = "com.widgetg7.mobile"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        buildConfigField("String", "RELAY_BASE_URL", "\"$relayBaseUrl\"")
        buildConfigField("String", "RELAY_BEARER_TOKEN", "\"$relayBearerToken\"")
        buildConfigField("String", "DEXCOM_SHARE_APPLICATION_ID", "\"$dexcomShareApplicationId\"")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.google.android.gms:play-services-wearable:18.2.0")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
}
