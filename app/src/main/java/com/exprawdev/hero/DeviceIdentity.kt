package com.exprawdev.hero

import android.content.Context
import java.util.UUID

class DeviceIdentity(context: Context) {

    private val preferences = context.getSharedPreferences(
        "hero_device_identity",
        Context.MODE_PRIVATE
    )

    fun getDeviceId(): String {
        val savedId = preferences.getString("device_id", null)

        if (savedId != null) {
            return savedId
        }

        val newId = UUID.randomUUID().toString()

        preferences.edit()
            .putString("device_id", newId)
            .apply()

        return newId
    }

    fun hasDeviceId(): Boolean {
        return preferences.getString("device_id", null) != null
    }

    fun resetDeviceId() {
        preferences.edit()
            .remove("device_id")
            .apply()
    }
}
