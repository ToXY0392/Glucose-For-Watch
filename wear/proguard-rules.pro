# Glucose For Watch — wear release (R8 / ProGuard)
# Keep TileService, ProtoLayout pipeline, Data Layer listener, AGP colors.

# --- Framework entry points (bound by class name) ---
-keep class * extends androidx.wear.tiles.TileService { *; }
-keep class com.glucoseforwatch.wear.tile.GlucoseTileServiceV2 { *; }
-keep class com.glucoseforwatch.wear.tile.GlucoseRefreshActivity { *; }

-keep class * extends androidx.wear.watchface.complications.datasource.ComplicationDataSourceService { *; }
-keep class * extends androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService { *; }
-keep class com.glucoseforwatch.wear.complication.GlucoseComplicationServiceV2 { *; }
-keep class com.glucoseforwatch.wear.complication.GlucoseComplicationDataFactory { *; }
-keep class com.glucoseforwatch.wear.complication.GlucoseComplicationDataFactory$* { *; }
-keep class com.glucoseforwatch.wear.complication.ComplicationUpdateNotifier { *; }
-keep class com.glucoseforwatch.wear.complication.ComplicationInstanceRegistry { *; }
-keep class com.glucoseforwatch.wear.complication.ComplicationWakeRefresh { *; }
-keep class com.glucoseforwatch.wear.complication.ComplicationWakeRefresh$* { *; }
-keep class com.glucoseforwatch.wear.complication.AgpComplicationColorRamp { *; }
-keep class com.glucoseforwatch.wear.WearMainActivity { *; }
-keep class com.glucoseforwatch.wear.GlucoseForWatchWearApplication { *; }

-keep class * extends com.google.android.gms.wearable.WearableListenerService { *; }
-keep class com.glucoseforwatch.wear.services.WearDataLayerListenerService { *; }

# --- Tile / ProtoLayout pipeline (prevent R8$$REMOVED$$CLASS + constant inlining) ---
# Do not inline / merge these: Wear caches resource versions + layout identity under R8.
-keep class com.glucoseforwatch.wear.tile.GlucoseSimpleTileLayout { *; }
-keepclassmembers class com.glucoseforwatch.wear.tile.GlucoseSimpleTileLayout {
    public <methods>;
    public static <methods>;
}
-keep class com.glucoseforwatch.wear.tile.ToxyTileTheme { *; }
-keep class com.glucoseforwatch.wear.tile.ToxyTileTheme$* { *; }
-keepclassmembers class com.glucoseforwatch.wear.tile.ToxyTileTheme {
    public static <fields>;
    public static <methods>;
}
-keep class com.glucoseforwatch.wear.tile.GlucoseSyncCoordinator { *; }
-keep class com.glucoseforwatch.wear.tile.GlucoseSyncRequestExecutor { *; }
-keep class com.glucoseforwatch.wear.tile.GlucoseTileUpdateRequester { *; }
-keep class com.glucoseforwatch.wear.display.WearGlucoseSurfaceModel { *; }
-keep class com.glucoseforwatch.wear.display.WearGlucoseSurfaceModelFactory { *; }
-keep class com.glucoseforwatch.wear.complication.AgpComplicationColorRamp { *; }

# --- Cache / Data Layer models ---
-keep class com.glucoseforwatch.wear.data.GlucoseCache { *; }
-keep class com.glucoseforwatch.wear.data.GlucoseSnapshot { *; }
-keep class com.glucoseforwatch.wear.data.GlucoseKeys { *; }
-keep class com.glucoseforwatch.wear.data.RefreshStatusSnapshot { *; }
-keep class com.glucoseforwatch.wear.data.WatchSyncHealthSnapshot { *; }
-keep class com.glucoseforwatch.core.datalayer.GlucoseDataLayerContract { *; }

# --- AGP medical colors + formatters ---
-keep class com.glucoseforwatch.core.model.AgpGlucoseColors { *; }
-keep class com.glucoseforwatch.core.model.GlucoseRange { *; }
-keep class com.glucoseforwatch.core.model.GlucoseRangeResolver { *; }
-keep class com.glucoseforwatch.core.model.GlucoseDisplayUnit { *; }
-keep class com.glucoseforwatch.core.model.GlucoseUnitFormatter { *; }
-keepclassmembers enum com.glucoseforwatch.core.model.GlucoseRange {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers enum com.glucoseforwatch.core.model.GlucoseDisplayUnit {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Guava futures used by TileService.onTileRequest ---
-keep class com.google.common.util.concurrent.** { *; }
-dontwarn com.google.common.**

-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations
