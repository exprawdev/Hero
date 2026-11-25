
package com.exprawdev.hero

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class SafeZoneActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val accentColor = Color.rgb(0, 184, 217)

    private lateinit var latitudeInput: EditText
    private lateinit var longitudeInput: EditText
    private lateinit var radiusInput: EditText
    private lateinit var statusText: TextView

    private val preferences by lazy {
        getSharedPreferences("hero_safe_zone", Context.MODE_PRIVATE)
    }

    private val locationPermissionRequest = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "HERO SAFE ZONE"
            textSize = 25f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Configure and monitor your family's safe area"
            textSize = 13f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }

        latitudeInput = createInput("Latitude")
        longitudeInput = createInput("Longitude")
        radiusInput = createInput("Radius in meters")

        statusText = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(12, 20, 12, 20)
        }

        val currentLocationButton = Button(this).apply {
            text = "USE CURRENT LOCATION"
            setOnClickListener {
                getCurrentLocation()
            }
        }

        val saveButton = Button(this).apply {
            text = "SAVE SAFE ZONE"
            setOnClickListener {
                saveSafeZone()
            }
        }

        val permissionButton = Button(this).apply {
            text = "BACKGROUND LOCATION SETTINGS"
            setOnClickListener {
                openAppSettings()
            }
        }

        val enableButton = Button(this).apply {
            text = "ENABLE SAFE ZONE MONITORING"
            setOnClickListener {
                enableMonitoring()
            }
        }

        val disableButton = Button(this).apply {
            text = "DISABLE SAFE ZONE MONITORING"
            setOnClickListener {
                disableMonitoring()
            }
        }

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(subtitle)

        root.addView(createLabel("CENTER LATITUDE"))
        root.addView(latitudeInput)

        root.addView(createLabel("CENTER LONGITUDE"))
        root.addView(longitudeInput)

        root.addView(createLabel("SAFE ZONE RADIUS"))
        root.addView(radiusInput)

        root.addView(currentLocationButton)
        root.addView(saveButton)
        root.addView(permissionButton)
        root.addView(enableButton)
        root.addView(disableButton)
        root.addView(statusText)
        root.addView(backButton)

        setContentView(root)

        loadSafeZone()
    }

    override fun onResume() {
        super.onResume()
        refreshMonitoringStatus()
    }

    private fun createInput(hintText: String): EditText {
        return EditText(this).apply {
            hint = hintText
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setPadding(14, 12, 14, 12)
            setSingleLine(true)
        }
    }

    private fun createLabel(value: String): TextView {
        return TextView(this).apply {
            text = value
            textSize = 12f
            setTextColor(accentColor)
            setPadding(0, 14, 0, 6)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasBackgroundLocationPermission(): Boolean {
        return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q ||
            checkSelfPermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (!hasLocationPermission()) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationPermissionRequest
            )
            return
        }

        try {
            val manager = getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager

            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
            )

            var bestLocation: Location? = null

            for (provider in providers) {
                if (!manager.isProviderEnabled(provider)) {
                    continue
                }

                val location = manager.getLastKnownLocation(provider)

                if (
                    location != null &&
                    (
                        bestLocation == null ||
                        location.time > bestLocation.time
                    )
                ) {
                    bestLocation = location
                }
            }

            if (bestLocation != null) {
                latitudeInput.setText(bestLocation.latitude.toString())
                longitudeInput.setText(bestLocation.longitude.toString())

                statusText.text =
                    "Last known location loaded. Accuracy: " +
                    "${bestLocation.accuracy.toInt()} meters"
            } else {
                statusText.text =
                    "No recent location available. Enable GPS and try again."
            }

        } catch (_: SecurityException) {
            statusText.text = "Location permission is unavailable"
        } catch (_: Exception) {
            statusText.text = "Unable to get location"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == locationPermissionRequest) {
            if (hasLocationPermission()) {
                getCurrentLocation()
            } else {
                statusText.text = "Precise location permission denied"
            }
        }
    }

    private fun saveSafeZone() {
        val latitude = latitudeInput.text.toString().toDoubleOrNull()
        val longitude = longitudeInput.text.toString().toDoubleOrNull()
        val radius = radiusInput.text.toString().toDoubleOrNull()

        if (latitude == null || latitude !in -90.0..90.0) {
            showMessage("Enter a valid latitude between -90 and 90")
            return
        }

        if (longitude == null || longitude !in -180.0..180.0) {
            showMessage("Enter a valid longitude between -180 and 180")
            return
        }

        if (radius == null || radius <= 0.0 || radius > 100000.0) {
            showMessage("Enter a radius between 1 and 100000 meters")
            return
        }

        preferences.edit()
            .putString("latitude", latitude.toString())
            .putString("longitude", longitude.toString())
            .putString("radius", radius.toString())
            .putFloat("latitude", latitude.toFloat())
            .putFloat("longitude", longitude.toFloat())
            .putFloat("radius", radius.toFloat())
            .putBoolean("configured", true)
            .apply()

        statusText.text =
            "Safe Zone saved. Enable monitoring to apply this area."

        showMessage("Safe Zone saved on this device")
    }

    private fun loadSafeZone() {
        if (!preferences.getBoolean("configured", false)) {
            statusText.text = "No Safe Zone configured"
            return
        }

        val latitude = preferences.getFloat("latitude", 0f).toString()
        val longitude = preferences.getFloat("longitude", 0f).toString()
        val radius = preferences.getFloat("radius", 200f).toString()

        latitudeInput.setText(latitude)
        longitudeInput.setText(longitude)
        radiusInput.setText(radius)

        refreshMonitoringStatus()
    }

    private fun enableMonitoring() {
        if (!preferences.getBoolean("configured", false)) {
            showMessage("Save a Safe Zone before enabling monitoring")
            return
        }

        if (!hasLocationPermission()) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequest
            )
            return
        }

        if (!hasBackgroundLocationPermission()) {
            AlertDialog.Builder(this)
                .setTitle("Background Location Permission")
                .setMessage(
                    "To monitor Safe Zone entry and exit while HERO is not open, " +
                    "Android requires background location permission. " +
                    "Open app settings and select Allow all the time if available."
                )
                .setPositiveButton("OPEN SETTINGS") { _, _ ->
                    openAppSettings()
                }
                .setNegativeButton("CANCEL", null)
                .show()
            return
        }

        SafeZoneGeofenceManager.register(this) { success, message ->
            statusText.text = message
            showMessage(message)

            if (success) {
                refreshMonitoringStatus()
            }
        }
    }

    private fun disableMonitoring() {
        SafeZoneGeofenceManager.remove(this) { success, message ->
            statusText.text = message
            showMessage(message)
            refreshMonitoringStatus()
        }
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )

        startActivity(intent)
    }

    private fun refreshMonitoringStatus() {
        if (!::statusText.isInitialized) {
            return
        }

        if (!preferences.getBoolean("configured", false)) {
            statusText.text = "No Safe Zone configured"
            return
        }

        val enabled = preferences.getBoolean("geofence_enabled", false)

        statusText.text = if (enabled) {
            "SAFE ZONE CONFIGURED\n\nMonitoring registration succeeded.\n" +
                "Android may deliver entry and exit events with some delay."
        } else {
            "SAFE ZONE CONFIGURED\n\nMonitoring is not registered.\n" +
                "Save your area and enable monitoring."
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
