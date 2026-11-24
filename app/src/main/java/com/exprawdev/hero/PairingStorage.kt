
package com.exprawdev.hero

import android.content.Context

class PairingStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "hero_pairing",
        Context.MODE_PRIVATE
    )

    fun savePairing(
        deviceRole: String,
        familyId: String
    ) {
        preferences.edit()
            .putString("device_role", deviceRole)
            .putString("family_id", familyId)
            .putBoolean("is_paired", true)
            .apply()
    }

    fun isPaired(): Boolean {
        return preferences.getBoolean("is_paired", false)
    }

    fun getDeviceRole(): String? {
        return preferences.getString("device_role", null)
    }

    fun getFamilyId(): String? {
        return preferences.getString("family_id", null)
    }

    fun clearPairing() {
        preferences.edit().clear().apply()
    }
}
