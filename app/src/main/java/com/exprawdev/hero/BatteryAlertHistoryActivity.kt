
package com.exprawdev.hero

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BatteryAlertHistoryActivity : Activity() {

    private val preferences by lazy {
        getSharedPreferences(
            "hero_battery_alert_history",
            Context.MODE_PRIVATE
        )
    }

    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 28, 20, 20)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "BATTERY ALERT HISTORY"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }

        val clearButton = Button(this).apply {
            text = "CLEAR HISTORY"
            setOnClickListener {
                clearHistory()
            }
        }

        historyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scrollView = ScrollView(this)
        scrollView.addView(historyContainer)

        root.addView(title)
        root.addView(clearButton)
        root.addView(scrollView)

        setContentView(root)

        displayHistory()
    }

    private fun displayHistory() {
        historyContainer.removeAllViews()

        val records = loadRecords()

        if (records.length() == 0) {
            addHistoryText("No battery alerts recorded.")
            return
        }

        for (index in records.length() - 1 downTo 0) {
            val record = records.optJSONObject(index) ?: continue

            val percentage = record.optInt("percentage", -1)
            val threshold = record.optInt("threshold", -1)
            val timestamp = record.optLong("timestamp", 0L)

            val dateText = SimpleDateFormat(
                "dd MMM yyyy, HH:mm:ss",
                Locale.getDefault()
            ).format(Date(timestamp))

            val item = TextView(this).apply {
                text = "LOW BATTERY ALERT\n" +
                    "Battery: $percentage%\n" +
                    "Threshold: $threshold%\n" +
                    "Time: $dateText"

                textSize = 14f
                setTextColor(Color.WHITE)
                setPadding(16, 18, 16, 18)
                setBackgroundColor(Color.rgb(20, 31, 49))
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 6, 0, 6)
            historyContainer.addView(item, params)
        }
    }

    private fun addHistoryText(message: String) {
        val text = TextView(this).apply {
            this.text = message
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(16, 30, 16, 30)
        }

        historyContainer.addView(text)
    }

    private fun loadRecords(): JSONArray {
        val json = preferences.getString("records", "[]") ?: "[]"

        return try {
            JSONArray(json)
        } catch (_: Exception) {
            JSONArray()
        }
    }

    private fun clearHistory() {
        preferences.edit()
            .putString("records", "[]")
            .apply()

        displayHistory()
    }

    companion object {

        fun recordAlert(
            context: Context,
            percentage: Int,
            threshold: Int
        ) {
            val preferences = context.getSharedPreferences(
                "hero_battery_alert_history",
                Context.MODE_PRIVATE
            )

            val records = try {
                JSONArray(
                    preferences.getString("records", "[]") ?: "[]"
                )
            } catch (_: Exception) {
                JSONArray()
            }

            val record = JSONObject().apply {
                put("percentage", percentage)
                put("threshold", threshold)
                put("timestamp", System.currentTimeMillis())
            }

            records.put(record)

            val limitedRecords = JSONArray()
            val startIndex = (records.length() - 100).coerceAtLeast(0)

            for (index in startIndex until records.length()) {
                limitedRecords.put(records.getJSONObject(index))
            }

            preferences.edit()
                .putString("records", limitedRecords.toString())
                .apply()
        }
    }
}
