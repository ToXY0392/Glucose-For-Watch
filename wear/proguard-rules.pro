-keep class * extends androidx.wear.tiles.TileService { *; }
-keep class * extends androidx.wear.watchface.complications.datasource.ComplicationDataSourceService { *; }
-keep class * extends androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService { *; }
-keep class com.glucoseforwatch.wear.complication.GlucoseComplicationService { *; }

# Wear Data Layer listeners (release minify)
-keep class * extends com.google.android.gms.wearable.WearableListenerService { *; }
-keep class com.glucoseforwatch.wear.services.WearDataLayerListenerService { *; }
-keep class com.glucoseforwatch.mobile.sync.PhoneWearRefreshRequestService { *; }
