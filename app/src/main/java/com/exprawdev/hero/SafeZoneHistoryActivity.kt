
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SafeZoneHistoryActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val accentColor = Color.rgb(0, 184, 217)

    private lateinit var historyContainer: LinearLayout
    private lateinit var summaryText: TextView

    private val preferences by lazy {
        getSharedPreferences(
            "hero_safe_zone_history",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "SAFE ZONE HISTORY"
            textSize = 23f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 16)
        }

        summaryText = TextView(this).apply {
            textSize = 14f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            setPadding(12, 12, 12, 16)
        }

        historyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scrollView = ScrollView(this).apply {
            addView(historyContainer)
        }

        val clearButton = Button(this).apply {
            text = "CLEAR HISTORY"
            setOnClickListener {
                confirmClearHistory()
            }
        }

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(summaryText)
        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
        root.addView(clearButton)
        root.addView(backButton)

        setContentView(root)

        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        historyContainer.removeAllViews()

        val records = try {
            JSONArray(
                preferences.getString("records", "[]") ?: "[]"
            )
        } catch (_: Exception) {
            JSONArray()
        }

        summaryText.text =
            "TOTAL EVENTS: ${records.length()}\n" +
            "Showing the latest Safe Zone events"

        if (records.length() == 0) {
            val emptyText = TextView(this).apply {
                text = "No Safe Zone events recorded yet."
                textSize = 15f
                setTextColor(Color.LTGRAY)
                gravity = Gravity.CENTER
                setPadding(12, 40, 12, 40)
            }

            historyContainer.addView(emptyText)
            return
        }

        for (index in records.length() - 1 downTo 0) {
            val record = records.optJSONObject(index) ?: continue

            val event = record.optString(
                "event",
                "Unknown event"
            )

            val timestamp = record.optLong(
                "timestamp",
                0L
            )

            val dateText = if (timestamp > 0L) {
                SimpleDateFormat(
                    "dd MMM yyyy, HH:mm:ss",
                    Locale.getDefault()
                ).format(Date(timestamp))
            } else {
                "Unknown time"
            }

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 14, 16, 14)
                setBackgroundColor(Color.rgb(19, 29, 45))
            }

            val eventText = TextView(this).apply {
                text = event
                textSize = 16f
                setTextColor(
                    if (event.contains("left", true)) {
                        Color.rgb(255, 170, 80)
                    } else {
                        accentColor
                    }
                )
            }

            val dateView = TextView(this).apply {
                text = dateText
                textSize = 12f
                setTextColor(Color.LTGRAY)
                setPadding(0, 8, 0, 0)
            }

            card.addView(eventText)
            card.addView(dateView)

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12)
            }

            historyContainer.addView(card, cardParams)
        }
    }

    private fun confirmClearHistory() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Clear Safe Zone History")
            .setMessage(
                "Are you sure you want to delete all saved Safe Zone events?"
            )
            .setPositiveButton("CLEAR") { _, _ ->
                preferences.edit()
                    .putString("records", "[]")
                    .apply()

                loadHistory()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }
}
