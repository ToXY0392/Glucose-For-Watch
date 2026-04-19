package com.widgetg7.mobile.data

import com.widgetg7.mobile.dexcom.DexcomShareConfigProvider
import com.widgetg7.mobile.dexcom.DexcomSharePhoneGlucoseSource
import com.widgetg7.mobile.relay.RelayConfigProvider
import com.widgetg7.mobile.relay.RelayPhoneGlucoseSource

object PhoneGlucoseSourceFactory {
    fun create(): PhoneGlucoseSource {
        val dexcomShareConfig = DexcomShareConfigProvider.fromBuildConfig()
        val relayConfig = RelayConfigProvider.fromBuildConfig()

        return if (dexcomShareConfig.isConfigured()) {
            DexcomSharePhoneGlucoseSource(dexcomShareConfig)
        } else if (relayConfig.isConfigured()) {
            RelayPhoneGlucoseSource(relayConfig)
        } else {
            DemoPhoneGlucoseSource()
        }
    }
}