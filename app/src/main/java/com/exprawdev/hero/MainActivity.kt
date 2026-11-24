package com.exprawdev.hero

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val primaryColor = Color.rgb(23, 105, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "HERO"
            textSize = 42f
            setTextColor(primaryColor)
            gravity = Gravity.CENTER
            letterSpacing = 0.18f
        }

        val subtitle = TextView(this).apply {
            text = "FAMILY SAFETY"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "Protect what matters most."
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 30)
        }

        val enterButton = Button(this).apply {
            text = "GET STARTED"
            textSize = 15f
            setTextColor(Color.WHITE)
            setBackgroundColor(primaryColor)

            setOnClickListener {
                val intent = Intent(
                    this@MainActivity,
                    DashboardActivity::class.java
                )
                startActivity(intent)
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(description)

        root.addView(
            enterButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            )
        )

        setContentView(root)
    }
}
