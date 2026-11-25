
package com.exprawdev.hero

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class BatteryMonitorActivity : Activity() {

    private lateinit var batteryLevel: TextView
    private lateinit var batteryStatus: TextView
    private lateinit var chargingSource: TextView
    private lateinit var batteryTemperature: TextView
    private lateinit var batteryVoltage: TextView
    private lateinit var batteryTechnology: TextView

    private var receiverRegistered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            val level = intent.getIntExtra(
                BatteryManager.EXTRA_LEVEL, -1
            )
            val scale = intent.getIntExtra(
                BatteryManager.EXTRA_SCALE, 100
            )

            val percentage = if (level >= 0 && scale > 0) {
                (level * 100) / scale
            } else {
                -1
            }

            val status = intent.getIntExtra(
                BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN
            )

            val plugged = intent.getIntExtra(
                BatteryManager.EXTRA_PLUGGED, 0
            )

            val temperature = intent.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE, -1
            )

            val voltage = intent.getIntExtra(
                BatteryManager.EXTRA_VOLTAGE, -1
            )

            val technology = intent.getStringExtra(
                BatteryManager.EXTRA_TECHNOLOGY
            ) ?: "Unknown"

            batteryLevel.text = if (percentage >= 0) {
                "$percentage%"
            } else {
                "Unavailable"
            }

            batteryStatus.text = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Fully charged"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
                else -> "Unknown"
            }

            chargingSource.text = when (plugged) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC charger"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                else -> "Not connected"
            }

            batteryTemperature.text = if (temperature >= 0) {
                "${temperature / 10.0} °C"
            } else {
                "Unavailable"
            }

            batteryVoltage.text = if (voltage >= 0) {
                "$voltage mV"
            } else {
                "Unavailable"
            }

            batteryTechnology.text = technology
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "BATTERY MONITOR"
            textSize = 23f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Device battery information"
            textSize = 13f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 30)
        }

        batteryLevel = createValueText(42f)
        batteryStatus = createValueText(18f)
        chargingSource = createValueText(18f)
        batteryTemperature = createValueText(18f)
        batteryVoltage = createValueText(18f)
        batteryTechnology = createValueText(18f)

        root.addView(title)
        root.addView(subtitle)

        addInfoCard(root, "BATTERY LEVEL", batteryLevel)
        addInfoCard(root, "CHARGING STATUS", batteryStatus)
        addInfoCard(root, "POWER SOURCE", chargingSource)
        addInfoCard(root, "BATTERY TEMPERATURE", batteryTemperature)
        addInfoCard(root, "BATTERY VOLTAGE", batteryVoltage)
        addInfoCard(root, "BATTERY TECHNOLOGY", batteryTechnology)

        setContentView(root)
    }

    private fun createValueText(size: Float): TextView {
        return TextView(this).apply {
            text = "Loading..."
            textSize = size
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
    }

    private fun addInfoCard(
        parent: LinearLayout,
        label: String,
        value: TextView
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 14, 16, 14)
            setBackgroundColor(Color.rgb(20, 30, 48))
        }

        val heading = TextView(this).apply {
            text = label
            textSize = 11f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
        }

        card.addView(heading)
        card.addView(value)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        parent.addView(card, params)
    }

    override fun onStart() {
        super.onStart()

        if (!receiverRegistered) {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

            registerReceiver(batteryReceiver, filter)
            receiverRegistered = true
        }
    }

    override fun onStop() {
        if (receiverRegistered) {
            unregisterReceiver(batteryReceiver)
            receiverRegistered = false
        }

        super.onStop()
    }
}
