
package com.exprawdev.hero

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class BatteryAlertActivity : Activity() {

    private val preferences by lazy {
        getSharedPreferences("hero_battery_alert", Context.MODE_PRIVATE)
    }

    private lateinit var thresholdValue: TextView
    private lateinit var thresholdSeekBar: SeekBar
    private lateinit var alertSwitch: Switch
    private lateinit var alertStatus: TextView

    private var selectedThreshold = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedThreshold = preferences.getInt("threshold", 20)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "BATTERY ALERT SETTINGS"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Configure your battery alerts"
            textSize = 13f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 30)
        }

        thresholdValue = TextView(this).apply {
            textSize = 38f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "Choose the battery percentage that triggers a low battery alert."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 16, 8, 20)
        }

        thresholdSeekBar = SeekBar(this).apply {
            max = 95
            progress = selectedThreshold - 5

            setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {

                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        selectedThreshold = progress + 5
                        updateThresholdDisplay()
                    }

                    override fun onStartTrackingTouch(
                        seekBar: SeekBar?
                    ) {
                    }

                    override fun onStopTrackingTouch(
                        seekBar: SeekBar?
                    ) {
                    }
                }
            )
        }

        val rangeLabel = TextView(this).apply {
            text = "5%                                      100%"
            textSize = 12f
            setTextColor(Color.LTGRAY)
        }

        alertSwitch = Switch(this).apply {
            text = "Enable battery notifications"
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = preferences.getBoolean("alerts_enabled", true)
            setPadding(0, 24, 0, 24)
        }

        val saveButton = Button(this).apply {
            text = "SAVE BATTERY SETTINGS"
            setOnClickListener {
                saveSettings()
            }
        }

        val resetButton = Button(this).apply {
            text = "RESET TO DEFAULT"
            setOnClickListener {
                selectedThreshold = 20
                thresholdSeekBar.progress = 15
                alertSwitch.isChecked = true
                saveSettings()
            }
        }

        alertStatus = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 24, 8, 8)
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(thresholdValue)
        root.addView(description)
        root.addView(thresholdSeekBar)
        root.addView(rangeLabel)
        root.addView(alertSwitch)
        root.addView(saveButton)
        root.addView(resetButton)
        root.addView(alertStatus)

        setContentView(root)

        updateThresholdDisplay()
        updateSavedStatus()
    }

    private fun updateThresholdDisplay() {
        thresholdValue.text = "$selectedThreshold%"
    }

    private fun saveSettings() {
        preferences.edit()
            .putInt("threshold", selectedThreshold)
            .putBoolean("alerts_enabled", alertSwitch.isChecked)
            .apply()

        updateSavedStatus()

        Toast.makeText(
            this,
            "Battery settings saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateSavedStatus() {
        val savedThreshold = preferences.getInt("threshold", 20)
        val enabled = preferences.getBoolean("alerts_enabled", true)

        alertStatus.text =
            "Threshold: $savedThreshold%\n" +
            "Notifications: ${if (enabled) "Enabled" else "Disabled"}"
    }

    companion object {
        fun getThreshold(context: Context): Int {
            return context.getSharedPreferences(
                "hero_battery_alert",
                Context.MODE_PRIVATE
            ).getInt("threshold", 20)
        }
    }
}
