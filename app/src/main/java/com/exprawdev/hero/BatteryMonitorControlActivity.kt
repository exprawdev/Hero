
package com.exprawdev.hero

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class BatteryMonitorControlActivity : Activity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "BATTERY MONITOR CONTROL"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }

        val description = TextView(this).apply {
            text = "Control battery monitoring on this device. " +
                "A persistent Android notification will be shown " +
                "while the monitoring service is active."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 24)
        }

        statusText = TextView(this).apply {
            text = "Choose an action below."
            textSize = 15f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(8, 20, 8, 20)
        }

        val startButton = Button(this).apply {
            text = "START BATTERY MONITORING"
            setOnClickListener {
                startMonitoring()
            }
        }

        val stopButton = Button(this).apply {
            text = "STOP BATTERY MONITORING"
            setOnClickListener {
                stopMonitoring()
            }
        }

        root.addView(title)
        root.addView(description)
        root.addView(statusText)
        root.addView(startButton)
        root.addView(stopButton)

        setContentView(root)
    }

    private fun startMonitoring() {
        if (
            Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST
            )
            return
        }

        launchMonitoringService()
    }

    private fun launchMonitoringService() {
        val serviceIntent = Intent(
            this,
            BatteryMonitorService::class.java
        ).apply {
            action = BatteryMonitorService.ACTION_START
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

            statusText.text =
                "Battery monitoring start requested."

            Toast.makeText(
                this,
                "Battery monitoring start requested",
                Toast.LENGTH_SHORT
            ).show()

        } catch (exception: Exception) {
            statusText.text =
                "Unable to start monitoring: ${exception.message}"

            Toast.makeText(
                this,
                "Could not start battery monitoring",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun stopMonitoring() {
        val serviceIntent = Intent(
            this,
            BatteryMonitorService::class.java
        ).apply {
            action = BatteryMonitorService.ACTION_STOP
        }

        try {
            startService(serviceIntent)

            statusText.text =
                "Battery monitoring stop requested."

            Toast.makeText(
                this,
                "Battery monitoring stop requested",
                Toast.LENGTH_SHORT
            ).show()

        } catch (exception: Exception) {
            statusText.text =
                "Unable to stop monitoring: ${exception.message}"
        }
    }

    @Deprecated("Use the platform permission flow")
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

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                launchMonitoringService()
            } else {
                statusText.text =
                    "Notification permission is required for this setup."

                Toast.makeText(
                    this,
                    "Please allow notifications to continue",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST = 7501
    }
}
