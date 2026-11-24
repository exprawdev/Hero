package com.exprawdev.hero

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PairingActivity : AppCompatActivity() {

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
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 32)
            setBackgroundColor(background)
        }

        val title = TextView(this).apply {
            text = "DEVICE PAIRING"
            textSize = 25f
            setTextColor(blue)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "Connect family devices securely."
            textSize = 15f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 28)
        }

        val parentButton = createButton("PARENT DEVICE")
        val childButton = createButton("CHILD DEVICE")

        val information = TextView(this).apply {
            text = "Choose the role for this device. Both devices must be paired with the knowledge and consent of their users."
            textSize = 13f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(12, 24, 12, 0)
        }

        parentButton.setOnClickListener {
            showMessage("Parent device selected. Secure pairing will be configured next.")
        }

        childButton.setOnClickListener {
            showMessage("Child device selected. The device user must review and approve pairing.")
        }

        root.addView(title)
        root.addView(description)
        root.addView(parentButton)
        root.addView(childButton)
        root.addView(information)

        setContentView(root)
    }

    private fun createButton(label: String): Button {
        return Button(this).apply {
            text = label
            textSize = 14f
            setTextColor(white)
            setBackgroundColor(blue)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            ).apply {
                bottomMargin = 14
            }
        }
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(
            this,
            message,
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
}
