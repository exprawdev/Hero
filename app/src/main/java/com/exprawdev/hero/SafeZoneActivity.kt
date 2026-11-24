
package com.exprawdev.hero

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class SafeZoneActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(20, 30, 48)
    private val accentColor = Color.rgb(0, 184, 217)

    private lateinit var latitudeInput: EditText
    private lateinit var longitudeInput: EditText
    private lateinit var radiusInput: EditText
    private lateinit var statusText: TextView

    private val preferences by lazy {
        getSharedPreferences("hero_safe_zone", Context.MODE_PRIVATE)
    }

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
            text = "Configure your family's safe area"
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

        val saveButton = Button(this).apply {
            text = "SAVE SAFE ZONE"
            setOnClickListener {
                saveSafeZone()
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

        root.addView(saveButton)
        root.addView(statusText)
        root.addView(backButton)

        setContentView(root)

        loadSafeZone()
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

    private fun saveSafeZone() {
        val latitude = latitudeInput.text.toString().toDoubleOrNull()
        val longitude = longitudeInput.text.toString().toDoubleOrNull()
        val radius = radiusInput.text.toString().toDoubleOrNull()

        if (latitude == null || latitude !in -90.0..90.0) {
            showError("Enter a valid latitude between -90 and 90")
            return
        }

        if (longitude == null || longitude !in -180.0..180.0) {
            showError("Enter a valid longitude between -180 and 180")
            return
        }

        if (radius == null || radius <= 0.0 || radius > 100000.0) {
            showError("Enter a radius between 1 and 100000 meters")
            return
        }

        preferences.edit()
            .putString("latitude", latitude.toString())
            .putString("longitude", longitude.toString())
            .putString("radius", radius.toString())
            .putBoolean("configured", true)
            .apply()

        updateStatus(latitude, longitude, radius)

        Toast.makeText(
            this,
            "Safe zone saved on this device",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadSafeZone() {
        if (!preferences.getBoolean("configured", false)) {
            statusText.text = "No safe zone configured"
            return
        }

        val latitude = preferences.getString("latitude", "") ?: ""
        val longitude = preferences.getString("longitude", "") ?: ""
        val radius = preferences.getString("radius", "") ?: ""

        latitudeInput.setText(latitude)
        longitudeInput.setText(longitude)
        radiusInput.setText(radius)

        updateStatus(
            latitude.toDoubleOrNull() ?: return,
            longitude.toDoubleOrNull() ?: return,
            radius.toDoubleOrNull() ?: return
        )
    }

    private fun updateStatus(
        latitude: Double,
        longitude: Double,
        radius: Double
    ) {
        statusText.text = """
            SAFE ZONE CONFIGURED

            Latitude: $latitude
            Longitude: $longitude
            Radius: ${radius.toInt()} meters

            Local configuration saved.
            Background monitoring is not active yet.
        """.trimIndent()
    }

    private fun showError(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
