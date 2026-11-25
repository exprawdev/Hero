
package com.exprawdev.hero

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

object SafeZoneGeofenceManager {

    private const val REQUEST_CODE = 9102
    private const val GEOFENCE_ID = "hero_safe_zone"

    fun register(
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        val appContext = context.applicationContext

        val preferences = appContext.getSharedPreferences(
            "hero_safe_zone",
            Context.MODE_PRIVATE
        )

        if (!preferences.getBoolean("configured", false)) {
            onResult(false, "Please save a Safe Zone first.")
            return
        }

        if (
            appContext.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onResult(false, "Precise location permission is required.")
            return
        }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            appContext.checkSelfPermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onResult(
                false,
                "Background location permission is required. Grant it in Android app settings."
            )
            return
        }

        val latitude = preferences.getFloat(
            "latitude",
            0f
        ).toDouble()

        val longitude = preferences.getFloat(
            "longitude",
            0f
        ).toDouble()

        val radius = preferences.getFloat(
            "radius",
            200f
        )

        if (
            latitude !in -90.0..90.0 ||
            longitude !in -180.0..180.0 ||
            radius <= 0f
        ) {
            onResult(false, "Safe Zone coordinates or radius are invalid.")
            return
        }

        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(
                latitude,
                longitude,
                radius
            )
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                    Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .setExpirationDuration(
                Geofence.NEVER_EXPIRE
            )
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(
                GeofencingRequest.INITIAL_TRIGGER_ENTER
            )
            .addGeofence(geofence)
            .build()

        LocationServices
            .getGeofencingClient(appContext)
            .addGeofences(
                request,
                createPendingIntent(appContext)
            )
            .addOnSuccessListener {
                preferences.edit()
                    .putBoolean("geofence_enabled", true)
                    .apply()

                onResult(
                    true,
                    "Safe Zone monitoring was registered successfully."
                )
            }
            .addOnFailureListener { error ->
                preferences.edit()
                    .putBoolean("geofence_enabled", false)
                    .apply()

                onResult(
                    false,
                    "Could not register Safe Zone: ${error.localizedMessage ?: "Unknown error"}"
                )
            }
    }

    fun remove(
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        val appContext = context.applicationContext

        LocationServices
            .getGeofencingClient(appContext)
            .removeGeofences(
                createPendingIntent(appContext)
            )
            .addOnSuccessListener {
                appContext.getSharedPreferences(
                    "hero_safe_zone",
                    Context.MODE_PRIVATE
                ).edit()
                    .putBoolean("geofence_enabled", false)
                    .apply()

                onResult(
                    true,
                    "Safe Zone monitoring has been disabled."
                )
            }
            .addOnFailureListener { error ->
                onResult(
                    false,
                    "Could not remove Safe Zone: ${error.localizedMessage ?: "Unknown error"}"
                )
            }
    }

    private fun createPendingIntent(
        context: Context
    ): PendingIntent {
        val intent = Intent(
            context,
            SafeZoneGeofenceReceiver::class.java
        )

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            flags
        )
    }
}
