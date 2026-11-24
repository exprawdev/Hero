
package com.exprawdev.hero

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class LocationActivity : Activity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var locationText: TextView
    private lateinit var statusText: TextView

    private var currentLatitude = Double.NaN
    private var currentLongitude = Double.NaN

    private val locationPermissionRequest = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 32)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "HERO LIVE LOCATION"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        statusText = TextView(this).apply {
            text = "Location permission is required"
            textSize = 15f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 20)
        }

        locationText = TextView(this).apply {
            text = "Latitude: --\nLongitude: --\nAccuracy: --"
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 30)
        }

        val permissionButton = Button(this).apply {
            text = "ENABLE LOCATION"
            setOnClickListener {
                requestLocationPermission()
            }
        }

        val refreshButton = Button(this).apply {
            text = "REFRESH LOCATION"
            setOnClickListener {
                startLocationUpdates()
            }
        }

        val mapButton = Button(this).apply {
            text = "OPEN MAP"
            setOnClickListener {
                openMap()
            }
        }

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(statusText)
        root.addView(locationText)
        root.addView(permissionButton)
        root.addView(refreshButton)
        root.addView(mapButton)
        root.addView(backButton)

        setContentView(root)

        if (hasLocationPermission()) {
            startLocationUpdates()
        }
    }

    private fun hasLocationPermission(): Boolean {
        val finePermission = checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission = checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return finePermission || coarsePermission
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationPermissionRequest
        )
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
                startLocationUpdates()
            } else {
                statusText.text = "Location permission denied"
            }
        }
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }

        try {
            val provider = when {
                locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                ) -> LocationManager.GPS_PROVIDER

                locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                ) -> LocationManager.NETWORK_PROVIDER

                else -> null
            }

            if (provider == null) {
                statusText.text = "Please enable GPS or location services"
                return
            }

            statusText.text = "Getting device location..."

            locationManager.requestLocationUpdates(
                provider,
                3000L,
                5f,
                this
            )

            val lastLocation =
                locationManager.getLastKnownLocation(provider)

            if (lastLocation != null) {
                onLocationChanged(lastLocation)
            }

        } catch (exception: SecurityException) {
            statusText.text = "Location permission is unavailable"
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLatitude = location.latitude
        currentLongitude = location.longitude

        statusText.text = "Location updated"

        locationText.text = """
            Latitude: $currentLatitude

            Longitude: $currentLongitude

            Accuracy: ${location.accuracy} meters
        """.trimIndent()
    }

    private fun openMap() {
        if (currentLatitude.isNaN() || currentLongitude.isNaN()) {
            Toast.makeText(
                this,
                "Location is not available yet",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(this, LocationMapActivity::class.java).apply {
            putExtra("latitude", currentLatitude)
            putExtra("longitude", currentLongitude)
        }

        startActivity(intent)
    }

    override fun onProviderDisabled(provider: String) {
        statusText.text = "Location provider disabled"
    }

    override fun onProviderEnabled(provider: String) {
        statusText.text = "Location provider enabled"
    }

    override fun onStatusChanged(
        provider: String?,
        status: Int,
        extras: Bundle?
    ) {
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    override fun onDestroy() {
        locationManager.removeUpdates(this)
        super.onDestroy()
    }
}
