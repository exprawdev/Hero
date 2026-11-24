package com.exprawdev.hero

import android.content.Context

enum class DeviceRole {
    PARENT,
    CHILD
}

class DeviceRoleStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "hero_device_role",
        Context.MODE_PRIVATE
    )

    fun saveRole(role: DeviceRole) {
        preferences.edit()
            .putString("device_role", role.name)
            .apply()
    }

    fun getRole(): DeviceRole? {
        val savedRole = preferences.getString("device_role", null)

        return try {
            savedRole?.let { DeviceRole.valueOf(it) }
        } catch (exception: IllegalArgumentException) {
            null
        }
    }

    fun isRoleSelected(): Boolean {
        return getRole() != null
    }

    fun clearRole() {
        preferences.edit()
            .remove("device_role")
            .apply()
    }
}
