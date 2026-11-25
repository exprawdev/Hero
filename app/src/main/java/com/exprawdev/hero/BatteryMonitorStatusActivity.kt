
package com.exprawdev.hero

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class BatteryMonitorStatusActivity : Activity() {

    private lateinit var batteryLevelText: TextView
    private lateinit var chargingText: TextView
    private lateinit var temperatureText: TextView
    private lateinit var voltageText: TextView
    private lateinit var thresholdText: TextView
    private lateinit var alertText: TextView

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                updateBatteryInformation(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 32)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "BATTERY MONITOR STATUS"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 28)
        }

        batteryLevelText = createStatusText()
        chargingText = createStatusText()
        temperatureText = createStatusText()
        voltageText = createStatusText()
        thresholdText = createStatusText()
        alertText = createStatusText()

        root.addView(title)
        root.addView(batteryLevelText)
        root.addView(chargingText)
        root.addView(temperatureText)
        root.addView(voltageText)
        root.addView(thresholdText)
        root.addView(alertText)

        val scrollView = ScrollView(this)
        scrollView.addView(root)

        setContentView(scrollView)
    }

    private fun createStatusText(): TextView {
        return TextView(this).apply {
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(16, 18, 16, 18)
            setBackgroundColor(Color.rgb(20, 31, 49))
        }
    }

    private fun updateBatteryInformation(intent: Intent) {
        val level = intent.getIntExtra(
            BatteryManager.EXTRA_LEVEL,
            -1
        )

        val scale = intent.getIntExtra(
            BatteryManager.EXTRA_SCALE,
            -1
        )

        if (level < 0 || scale <= 0) return

        val percentage = level * 100 / scale

        val status = intent.getIntExtra(
            BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN
        )

        val chargingStatus = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "Fully charged"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            else -> "Unknown"
        }

        val temperature = intent.getIntExtra(
            BatteryManager.EXTRA_TEMPERATURE,
            -1
        )

        val voltage = intent.getIntExtra(
            BatteryManager.EXTRA_VOLTAGE,
            -1
        )

        val threshold = BatteryAlertActivity.getThreshold(this)

        val preferences = getSharedPreferences(
            "hero_battery_alert",
            Context.MODE_PRIVATE
        )

        val alertsEnabled = preferences.getBoolean(
            "alerts_enabled",
            true
        )

        batteryLevelText.text = "Battery Level: $percentage%"
        chargingText.text = "Charging Status: $chargingStatus"

        temperatureText.text =
            if (temperature >= 0) {
                "Battery Temperature: ${temperature / 10.0} °C"
            } else {
                "Battery Temperature: Unavailable"
            }

        voltageText.text =
            if (voltage > 0) {
                "Battery Voltage: $voltage mV"
            } else {
                "Battery Voltage: Unavailable"
            }

        thresholdText.text = "Alert Threshold: $threshold%"

        alertText.text =
            "Battery Alerts: ${if (alertsEnabled) "Enabled" else "Disabled"}\n" +
            "Current Condition: " +
            if (percentage <= threshold && alertsEnabled) {
                "Low Battery"
            } else {
                "Normal"
            }
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(
                batteryReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(batteryReceiver, filter)
        }
    }

    override fun onStop() {
        try {
            unregisterReceiver(batteryReceiver)
        } catch (_: IllegalArgumentException) {
        }

        super.onStop()
    }
}
