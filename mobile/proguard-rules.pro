# Glucose For Watch — mobile release (R8 / ProGuard)
# Keep Data Layer listeners, sync FGS, and wire-format enums.

# --- Third-party ---
-keep class com.flyfishxu.kadb.** { *; }
-dontwarn com.flyfishxu.kadb.**

-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# --- Wear Data Layer (GMS binds by manifest class name) ---
-keep class * extends com.google.android.gms.wearable.WearableListenerService { *; }
-keep class com.glucoseforwatch.mobile.sync.PhoneWearRefreshRequestService { *; }

# Foreground sync + receivers (system / WorkManager entry points)
-keep class com.glucoseforwatch.mobile.sync.ActiveGlucoseSyncService { *; }
-keep class com.glucoseforwatch.mobile.sync.PhoneAutoSyncReceiver { *; }
-keep class com.glucoseforwatch.mobile.sync.PhoneBootReceiver { *; }
-keep class com.glucoseforwatch.mobile.worker.PhoneGlucoseSyncWorker { *; }

# Shared Data Layer contract + display unit on the wire
-keep class com.glucoseforwatch.core.datalayer.GlucoseDataLayerContract { *; }
-keep class com.glucoseforwatch.core.model.GlucoseDisplayUnit { *; }
-keepclassmembers enum com.glucoseforwatch.core.model.GlucoseDisplayUnit {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# AGP medical colors (must not be class-merged away in release)
-keep class com.glucoseforwatch.core.model.AgpGlucoseColors { *; }
-keep class com.glucoseforwatch.core.model.GlucoseRange { *; }
-keep class com.glucoseforwatch.core.model.GlucoseRangeResolver { *; }
-keepclassmembers enum com.glucoseforwatch.core.model.GlucoseRange {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations
