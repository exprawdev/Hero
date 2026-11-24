
package com.exprawdev.hero

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationHistoryActivity : Activity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var historyContainer: LinearLayout
    private lateinit var statusText: TextView

    private val permissionRequestCode = 2001
    private val preferencesName = "hero_location_history"
    private val historyKey = "location_records"
    private val maximumRecords = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "LOCATION HISTORY"
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 8, 0, 18)
        }

        statusText = TextView(this).apply {
            text = "Location history is stored on this device"
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(0, 184, 217))
            setPadding(0, 8, 0, 16)
        }

        val startButton = Button(this).apply {
            text = "START LOCATION RECORDING"
            setOnClickListener {
                requestLocationAccess()
            }
        }

        val refreshButton = Button(this).apply {
            text = "REFRESH HISTORY"
            setOnClickListener {
                displayHistory()
            }
        }

        val clearButton = Button(this).apply {
            text = "CLEAR LOCATION HISTORY"
            setOnClickListener {
                clearHistory()
            }
        }

        val scrollView = ScrollView(this)

        historyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 12, 0, 12)
        }

        scrollView.addView(historyContainer)

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(statusText)
        root.addView(startButton)
        root.addView(refreshButton)
        root.addView(clearButton)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(backButton)

        setContentView(root)

        displayHistory()
    }

    private fun requestLocationAccess() {
        if (hasLocationPermission()) {
            startRecording()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                permissionRequestCode
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
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

        if (requestCode == permissionRequestCode) {
            if (hasLocationPermission()) {
                startRecording()
            } else {
                statusText.text = "Location permission denied"
            }
        }
    }

    private fun startRecording() {
        if (!hasLocationPermission()) return

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
                statusText.text = "Please enable device location"
                return
            }

            locationManager.requestLocationUpdates(
                provider,
                60000L,
                20f,
                this
            )

            statusText.text = "Recording location while this page is active"

            val lastLocation =
                locationManager.getLastKnownLocation(provider)

            if (lastLocation != null) {
                saveLocation(lastLocation)
            }

        } catch (exception: SecurityException) {
            statusText.text = "Location permission unavailable"
        }
    }

    override fun onLocationChanged(location: Location) {
        saveLocation(location)
        statusText.text = "Location history updated"
    }

    private fun saveLocation(location: Location) {
        val preferences = getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        )

        val history = try {
            JSONArray(
                preferences.getString(historyKey, "[]") ?: "[]"
            )
        } catch (exception: Exception) {
            JSONArray()
        }

        val record = JSONObject().apply {
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("accuracy", location.accuracy)
            put("timestamp", System.currentTimeMillis())
        }

        val updatedHistory = JSONArray()

        val startIndex = maxOf(
            0,
            history.length() - maximumRecords + 1
        )

        for (index in startIndex until history.length()) {
            updatedHistory.put(history.getJSONObject(index))
        }

        updatedHistory.put(record)

        preferences.edit()
            .putString(historyKey, updatedHistory.toString())
            .apply()

        displayHistory()
    }

    private fun displayHistory() {
        historyContainer.removeAllViews()

        val preferences = getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        )

        val history = try {
            JSONArray(
                preferences.getString(historyKey, "[]") ?: "[]"
            )
        } catch (exception: Exception) {
            JSONArray()
        }

        if (history.length() == 0) {
            addHistoryText("No location history available")
            return
        }

        for (index in history.length() - 1 downTo 0) {
            val record = history.getJSONObject(index)

            val latitude = record.optDouble("latitude")
            val longitude = record.optDouble("longitude")
            val accuracy = record.optDouble("accuracy")
            val timestamp = record.optLong("timestamp")

            val date = SimpleDateFormat(
                "dd MMM yyyy, HH:mm:ss",
                Locale.getDefault()
            ).format(Date(timestamp))

            addHistoryText(
                """
                RECORD ${history.length() - index}

                Date: $date

                Latitude: $latitude

                Longitude: $longitude

                Accuracy: $accuracy meters
                """.trimIndent()
            )
        }
    }

    private fun addHistoryText(message: String) {
        val item = TextView(this).apply {
            text = message
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.rgb(20, 30, 48))
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        historyContainer.addView(item, params)
    }

    private fun clearHistory() {
        getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        ).edit()
            .remove(historyKey)
            .apply()

        displayHistory()

        Toast.makeText(
            this,
            "Location history cleared",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    override fun onDestroy() {
        locationManager.removeUpdates(this)
        super.onDestroy()
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
}
