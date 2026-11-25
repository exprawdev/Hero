
package com.exprawdev.hero

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

class BatteryAlertActivity : Activity() {

    private val preferences by lazy {
        getSharedPreferences("hero_battery_alert", Context.MODE_PRIVATE)
    }

    private lateinit var thresholdValue: TextView
    private lateinit var thresholdSeekBar: SeekBar
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
            text = "Configure your low battery threshold"
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
            text = "Show a battery alert when the battery level " +
                "reaches or falls below your selected percentage."
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
                updateThresholdDisplay()
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
        val saved = preferences.edit()
            .putInt("threshold", selectedThreshold)
            .apply()

        updateSavedStatus()

        Toast.makeText(
            this,
            "Battery threshold saved: $selectedThreshold%",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateSavedStatus() {
        val savedThreshold = preferences.getInt("threshold", 20)

        alertStatus.text =
            "Saved battery threshold: $savedThreshold%"
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
