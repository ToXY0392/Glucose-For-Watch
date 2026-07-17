# Kadb (ADB client) — conservé pour l’installation Wear en release
-keep class com.flyfishxu.kadb.** { *; }
-dontwarn com.flyfishxu.kadb.**

# ML Kit — reconnaissance de texte (assistant installation montre)
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Wear Data Layer listeners (release minify)
-keep class * extends com.google.android.gms.wearable.WearableListenerService { *; }
-keep class com.glucoseforwatch.wear.services.WearDataLayerListenerService { *; }
-keep class com.glucoseforwatch.mobile.sync.PhoneWearRefreshRequestService { *; }
