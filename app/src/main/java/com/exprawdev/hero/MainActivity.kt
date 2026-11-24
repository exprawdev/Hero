
package com.exprawdev.hero

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

class MainActivity : AppCompatActivity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val primaryColor = Color.rgb(23, 105, 255)
    private val textColor = Color.WHITE

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
            textSize = 38f
            setTextColor(primaryColor)
            gravity = Gravity.CENTER
            letterSpacing = 0.18f
        }

        val subtitle = TextView(this).apply {
            text = "FAMILY SAFETY"
            textSize = 14f
            setTextColor(textColor)
            gravity = Gravity.CENTER
            letterSpacing = 0.2f
        }

        val status = TextView(this).apply {
            text = "Your family protection starts here."
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)

        val space = android.view.View(this)
        root.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                28
            )
        )

        root.addView(status)

        setContentView(root)
    }
}
