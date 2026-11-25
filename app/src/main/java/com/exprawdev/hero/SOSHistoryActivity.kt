
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

class SOSHistoryActivity : Activity() {

    private lateinit var historyContainer: LinearLayout

    private val preferences by lazy {
        getSharedPreferences("hero_sos_history", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 28, 22, 28)
        }

        val title = TextView(this).apply {
            text = "SOS HISTORY"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Locally saved emergency message records"
            textSize = 13f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }

        val refreshButton = Button(this).apply {
            text = "REFRESH HISTORY"
            setOnClickListener {
                loadHistory()
            }
        }

        val clearButton = Button(this).apply {
            text = "CLEAR ALL HISTORY"
            setOnClickListener {
                clearHistory()
            }
        }

        historyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(refreshButton)
        root.addView(clearButton)
        root.addView(historyContainer)

        val note = TextView(this).apply {
            text = "These records show messages prepared in HERO. " +
                "They do not confirm that an SMS was sent or received."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            setPadding(4, 20, 4, 8)
        }

        root.addView(note)

        scrollView.addView(root)
        setContentView(scrollView)

        loadHistory()
    }

    private fun loadHistory() {
        historyContainer.removeAllViews()

        val records = readRecords()

        if (records.length() == 0) {
            addInfoCard(
                "NO SOS HISTORY",
                "No emergency messages have been recorded yet."
            )
            return
        }

        for (index in records.length() - 1 downTo 0) {
            val record = records.optJSONObject(index) ?: continue

            val phone = record.optString("phone", "Unknown")
            val message = record.optString("message", "")
            val timestamp = record.optLong("timestamp", 0L)

            val dateText = if (timestamp > 0L) {
                SimpleDateFormat(
                    "dd MMM yyyy, HH:mm",
                    Locale.getDefault()
                ).format(Date(timestamp))
            } else {
                "Unknown date"
            }

            addInfoCard(
                "SOS RECORD — $dateText",
                "Phone: $phone\n\nMessage:\n$message"
            )
        }
    }

    private fun addInfoCard(
        heading: String,
        detail: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 14, 16, 14)
            setBackgroundColor(Color.rgb(20, 30, 48))
        }

        val title = TextView(this).apply {
            text = heading
            textSize = 13f
            setTextColor(Color.rgb(0, 184, 217))
        }

        val content = TextView(this).apply {
            text = detail
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(0, 8, 0, 0)
        }

        card.addView(title)
        card.addView(content)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        historyContainer.addView(card, params)
    }

    private fun readRecords(): JSONArray {
        val savedData = preferences.getString("records", "[]")

        return try {
            JSONArray(savedData)
        } catch (_: Exception) {
            JSONArray()
        }
    }

    private fun clearHistory() {
        preferences.edit()
            .remove("records")
            .apply()

        loadHistory()
    }

    companion object {

        fun recordPreparedMessage(
            context: Context,
            phoneNumber: String,
            message: String
        ) {
            val preferences = context.getSharedPreferences(
                "hero_sos_history",
                Context.MODE_PRIVATE
            )

            val records = try {
                JSONArray(
                    preferences.getString("records", "[]")
                )
            } catch (_: Exception) {
                JSONArray()
            }

            val record = JSONObject().apply {
                put("phone", phoneNumber)
                put("message", message)
                put("timestamp", System.currentTimeMillis())
            }

            records.put(record)

            preferences.edit()
                .putString("records", records.toString())
                .apply()
        }
    }
}
