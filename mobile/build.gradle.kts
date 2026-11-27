import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.security.crypto)
    implementation(libs.play.services.wearable)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    debugImplementation(libs.showkase)
    debugImplementation(libs.showkase.annotation)
    kspDebug(libs.showkase.processor)

    debugImplementation(project(":core:testing"))

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
}
