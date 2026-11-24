package com.exprawdev.hero

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class LocationMapActivity : Activity() {

    private lateinit var mapView: WebView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val latitude = intent.getDoubleExtra(
            "latitude",
            Double.NaN
        )

        val longitude = intent.getDoubleExtra(
            "longitude",
            Double.NaN
        )

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "HERO LOCATION MAP"
            textSize = 21f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(16, 20, 16, 16)
        }

        statusText = TextView(this).apply {
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(0, 184, 217))
            setPadding(12, 8, 12, 12)
        }

        mapView = WebView(this).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = false
            settings.domStorageEnabled = false
        }

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(statusText)

        root.addView(
            mapView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(backButton)

        setContentView(root)

        if (latitude.isNaN() || longitude.isNaN()) {
            statusText.text = "No GPS coordinates received"
            mapView.loadData(
                "<html><body style='background:#080d18;color:white;text-align:center;padding:40px;font-family:sans-serif'><h3>Location unavailable</h3><p>Open the GPS location page first.</p></body></html>",
                "text/html",
                "UTF-8"
            )
        } else {
            statusText.text = "Latitude: $latitude | Longitude: $longitude"

            val mapUrl = "https://www.openstreetmap.org/" +
                "?mlat=$latitude&mlon=$longitude" +
                "#map=16/$latitude/$longitude"

            mapView.loadUrl(mapUrl)
        }
    }

    override fun onDestroy() {
        mapView.destroy()
        super.onDestroy()
    }
}
