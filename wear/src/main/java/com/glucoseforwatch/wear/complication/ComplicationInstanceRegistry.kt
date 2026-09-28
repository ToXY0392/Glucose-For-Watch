package com.glucoseforwatch.wear.complication

import android.content.Context
import androidx.annotation.Keep

/** Active complication slot IDs registered by the system (activate/deactivate callbacks). */
@Keep
internal object ComplicationInstanceRegistry {
    private const val PREFS = "widget_g7_complication_instances"

    fun register(context: Context, instanceId: Int) {
        val ids = loadIds(context).toMutableSet()
        if (ids.add(instanceId)) {
            persist(context, ids)
        }
    }

    fun unregister(context: Context, instanceId: Int) {
        val ids = loadIds(context).toMutableSet()
        if (ids.remove(instanceId)) {
            persist(context, ids)
        }
    }

    fun activeInstanceIds(context: Context): IntArray =
        loadIds(context).toIntArray()

    private fun loadIds(context: Context): Set<Int> {
        val raw = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("ids", "")
            .orEmpty()
        if (raw.isBlank()) return emptySet()
        return raw.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    private fun persist(context: Context, ids: Set<Int>) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("ids", ids.joinToString(","))
            .apply()
    }
}
