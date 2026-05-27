pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "GlucoseForWatch"
include(":mobile")
include(":wear")
include(":core:datalayer-contract")
include(":core:model")
include(":core:testing")
include(":feature:sync")
include(":feature:dexcom-share")
include(":feature:watch-install")
