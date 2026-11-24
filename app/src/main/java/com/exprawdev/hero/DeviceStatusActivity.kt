
package com.exprawdev.hero

import android.app.Activity
import android.os.Bundle
import android.os.BatteryManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class DeviceStatusActivity : Activity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 32)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "DEVICE STATUS"
            textSize = 25f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        statusText = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 30)
        }

        val refreshButton = Button(this).apply {
            text = "REFRESH STATUS"
            setOnClickListener {
                updateStatus()
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
        root.addView(refreshButton)
        root.addView(backButton)

        setContentView(root)

        updateStatus()
    }

    private fun updateStatus() {
        val batteryIntent = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val batteryLevel = batteryIntent?.getIntExtra(
            BatteryManager.EXTRA_LEVEL,
            -1
        ) ?: -1

        val batteryScale = batteryIntent?.getIntExtra(
            BatteryManager.EXTRA_SCALE,
            -1
        ) ?: -1

        val batteryPercent = if (batteryLevel >= 0 && batteryScale > 0) {
            batteryLevel * 100 / batteryScale
        } else {
            -1
        }

        val batteryStatus = batteryIntent?.getIntExtra(
            BatteryManager.EXTRA_STATUS,
            -1
        ) ?: -1

        val charging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
            batteryStatus == BatteryManager.BATTERY_STATUS_FULL

        val connectionManager = getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = connectionManager.activeNetwork
        val capabilities = connectionManager.getNetworkCapabilities(network)

        val internetAvailable = capabilities?.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) == true

        val connectionType = when {
            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) == true -> "Wi-Fi"

            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) == true -> "Mobile Data"

            else -> "Unknown"
        }

        val deviceName = Build.MODEL
        val androidVersion = Build.VERSION.RELEASE

        statusText.text = """
            DEVICE: $deviceName

            ANDROID VERSION: $androidVersion

            BATTERY: ${
                if (batteryPercent >= 0) "$batteryPercent%" else "Unknown"
            }

            POWER: ${if (charging) "Charging" else "Not charging"}

            INTERNET: ${if (internetAvailable) "Available" else "Unavailable"}

            CONNECTION: $connectionType
        """.trimIndent()
    }
}
