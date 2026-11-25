
package com.exprawdev.hero

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONArray

class BatteryAlertSummaryActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "BATTERY ALERT SUMMARY"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 12, 0, 28)
        }

        root.addView(title)

        val records = loadRecords()

        val totalAlerts = records.length()
        var lowestPercentage = 101
        var totalPercentage = 0
        var latestPercentage = -1
        var latestTimestamp = 0L

        for (index in 0 until records.length()) {
            val record = records.optJSONObject(index) ?: continue

            val percentage = record.optInt("percentage", -1)
            val timestamp = record.optLong("timestamp", 0L)

            if (percentage in 0..100) {
                lowestPercentage = minOf(
                    lowestPercentage,
                    percentage
                )

                totalPercentage += percentage
            }

            if (timestamp >= latestTimestamp) {
                latestTimestamp = timestamp
                latestPercentage = percentage
            }
        }

        val averagePercentage = if (totalAlerts > 0) {
            totalPercentage.toDouble() / totalAlerts
        } else {
            0.0
        }

        addSummaryCard(
            root,
            "TOTAL ALERTS",
            totalAlerts.toString()
        )

        addSummaryCard(
            root,
            "LATEST ALERT BATTERY",
            if (latestPercentage >= 0) {
                "$latestPercentage%"
            } else {
                "No data"
            }
        )

        addSummaryCard(
            root,
            "LOWEST RECORDED BATTERY",
            if (lowestPercentage <= 100) {
                "$lowestPercentage%"
            } else {
                "No data"
            }
        )

        addSummaryCard(
            root,
            "AVERAGE BATTERY AT ALERT",
            if (totalAlerts > 0) {
                String.format("%.1f%%", averagePercentage)
            } else {
                "No data"
            }
        )

        val note = TextView(this).apply {
            text = "This summary uses battery alerts stored locally on this device."
            textSize = 13f
            setTextColor(Color.LTGRAY)
            setPadding(4, 20, 4, 12)
        }

        root.addView(note)

        val scrollView = ScrollView(this)
        scrollView.addView(root)

        setContentView(scrollView)
    }

    private fun addSummaryCard(
        root: LinearLayout,
        label: String,
        value: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(Color.rgb(20, 31, 49))
        }

        val labelView = TextView(this).apply {
            text = label
            textSize = 13f
            setTextColor(Color.LTGRAY)
        }

        val valueView = TextView(this).apply {
            text = value
            textSize = 28f
            setTextColor(Color.WHITE)
            setPadding(0, 8, 0, 0)
        }

        card.addView(labelView)
        card.addView(valueView)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 6, 0, 10)
        root.addView(card, params)
    }

    private fun loadRecords(): JSONArray {
        val preferences = getSharedPreferences(
            "hero_battery_alert_history",
            Context.MODE_PRIVATE
        )

        val json = preferences.getString(
            "records",
            "[]"
        ) ?: "[]"

        return try {
            JSONArray(json)
        } catch (_: Exception) {
            JSONArray()
        }
    }
}
