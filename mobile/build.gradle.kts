import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val gfwVersionCode = providers.gradleProperty("gfwVersionCode").get().toInt()
val gfwVersionName = providers.gradleProperty("gfwVersionName").get()

android {
    namespace = "com.glucoseforwatch.mobile"
    compileSdk {
        // 37.1: Compose 1.13 (compose-bom 2026.11.01) requires it in its AAR metadata.
        version = release(37) { minorApiLevel = 1 }
    }

    defaultConfig {
        //noinspection SpellCheckingInspection
        val dexcomShareApplicationId =
            ((project.findProperty("dexcomShareApplicationId") as? String)
                ?: "d89443d2-327c-4a6f-89e5-496bbb0317db").replace("\"", "\\\"")

        applicationId = "com.glucoseforwatch.mobile"
        minSdk = 28
        targetSdk = 37
        versionCode = gfwVersionCode
        versionName = gfwVersionName

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
            // Local ProGuard/Doze validation only — no production keystore yet.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    packaging {
        resources {
            // BouncyCastle 1.86 (via kadb) ships the same license file in bcprov, bcpkix and bcutil.
            excludes += "META-INF/LICENSE.md"
        }
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

val embeddedWearAssetOutputDir =
    objects.directoryProperty().apply {
        set(layout.buildDirectory.dir("embeddedWearApk/wear"))
    }

val prepareWearApkForDebugAssets by tasks.registering(Copy::class) {
    description = "Copie wear-debug.apk → assets packagés (clé wear/glucose-for-watch-wear.apk)."
    group = "glucose for watch"
    dependsOn(":wear:assembleDebug")
    from(rootProject.layout.projectDirectory.file("wear/build/outputs/apk/debug/wear-debug.apk"))
    into(embeddedWearAssetOutputDir)
    rename { "glucose-for-watch-wear.apk" }
}

// AGP 9+ : assets générés — SourceSet API interdit les Provider ; Variant API relie la tâche au variant debug.
androidComponents {
    onVariants(selector().withBuildType("debug")) { variant ->
        variant.sources.assets?.addGeneratedSourceDirectory(
            prepareWearApkForDebugAssets,
        ) {
            embeddedWearAssetOutputDir
        }
    }
}
dependencies {
    implementation(project(":core:datalayer-contract"))
    implementation(project(":core:model"))
    implementation(project(":feature:sync"))
    implementation(project(":feature:dexcom-share"))
    implementation(project(":feature:watch-install"))
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("androidx.exifinterface:exifinterface:1.4.2")
    implementation("com.flyfishxu:kadb:2.1.5")
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.1")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.security:security-crypto:1.1.0")
    implementation("com.google.android.gms:play-services-wearable:20.0.1")
    implementation("androidx.wear:wear-remote-interactions:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    val composeBom = platform("androidx.compose:compose-bom:2026.11.01")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.1")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    val showkaseVersion = "1.0.5"
    debugImplementation("com.airbnb.android:showkase:$showkaseVersion")
    debugImplementation("com.airbnb.android:showkase-annotation:$showkaseVersion")
    kspDebug("com.airbnb.android:showkase-processor:$showkaseVersion")

    debugImplementation(project(":core:testing"))

    testImplementation(project(":core:testing"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation("org.robolectric:robolectric:4.18.1")
}
