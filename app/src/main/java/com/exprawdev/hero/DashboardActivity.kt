package com.exprawdev.hero

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private val background = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(19, 28, 45)
    private val blue = Color.rgb(23, 105, 255)
    private val white = Color.WHITE
    private val muted = Color.LTGRAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = background
        window.navigationBarColor = background

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(background)
        }

        val title = TextView(this).apply {
            text = "HERO"
            textSize = 30f
            setTextColor(blue)
            typeface = Typeface.DEFAULT_BOLD
        }

        val subtitle = TextView(this).apply {
            text = "Family Safety Dashboard"
            textSize = 15f
            setTextColor(muted)
        }

        root.addView(title)
        root.addView(subtitle)

        root.addView(
            createCard(
                "DEVICE STATUS",
                "No device connected yet"
            )
        )

        val section = TextView(this).apply {
            text = "FAMILY PROTECTION"
            textSize = 13f
            setTextColor(blue)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 28, 0, 12)
        }

        root.addView(section)

        val pairingCard = createCard(
            "Device Pairing",
            "Connect a parent or child device"
        )

        pairingCard.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardActivity,
                    PairingActivity::class.java
                )
            )
        }

        root.addView(pairingCard)

        val features = listOf(
            "Live Location" to "View shared location",
            "Screen Time" to "Manage daily usage",
            "App Management" to "Review installed apps",
            "Safe Zone" to "Manage trusted places",
            "Activity Report" to "View family activity",
            "Emergency SOS" to "Family emergency support",
            "Privacy Settings" to "Manage permissions"
        )

        features.forEach { (name, description) ->
            root.addView(createCard(name, description))
        }

        setContentView(root)
    }

    private fun createCard(
        title: String,
        description: String
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(18, 16, 18, 16)
            setBackgroundColor(cardColor)
            isClickable = true
            isFocusable = true
        }

        val heading = TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
        }

        val detail = TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(muted)
            setPadding(0, 6, 0, 0)
        }

        card.addView(heading)
        card.addView(detail)

        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        return card
    }
}
