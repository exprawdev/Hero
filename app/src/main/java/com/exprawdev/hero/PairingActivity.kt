package com.exprawdev.hero

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PairingActivity : AppCompatActivity() {

    private val background = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(19, 28, 45)
    private val blue = Color.rgb(23, 105, 255)
    private val white = Color.WHITE
    private val muted = Color.LTGRAY

    private lateinit var codeDisplay: TextView
    private lateinit var codeInput: EditText

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
            text = "Create a code on one device and enter it on the other."
            textSize = 14f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }

        codeDisplay = TextView(this).apply {
            text = "------"
            textSize = 36f
            setTextColor(white)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setPadding(20, 24, 20, 24)
            setBackgroundColor(cardColor)
        }

        val generateButton = createButton("GENERATE PAIRING CODE")

        codeInput = EditText(this).apply {
            hint = "Enter 6-digit pairing code"
            textSize = 16f
            setTextColor(white)
            setHintTextColor(muted)
            gravity = Gravity.CENTER
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(16, 12, 16, 12)
        }

        val verifyButton = createButton("CHECK CODE")

        generateButton.setOnClickListener {
            val code = PairingCode.generate()
            codeDisplay.text = code

            Toast.makeText(
                this,
                "Code generated. Share it only with the intended family member.",
                Toast.LENGTH_LONG
            ).show()
        }

        verifyButton.setOnClickListener {
            val code = codeInput.text.toString().trim()

            val message = if (PairingCode.isValid(code)) {
                "Code format is valid. Server verification is not connected yet."
            } else {
                "Please enter a valid 6-digit code."
            }

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()
        }

        root.addView(title)
        root.addView(description)
        root.addView(codeDisplay)

        addSpace(root, 20)
        root.addView(generateButton)

        addSpace(root, 20)
        root.addView(codeInput)

        addSpace(root, 12)
        root.addView(verifyButton)

        setContentView(root)
    }

    private fun createButton(label: String): Button {
        return Button(this).apply {
            text = label
            textSize = 13f
            setTextColor(white)
            setBackgroundColor(blue)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                56
            )
        }
    }

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {
        parent.addView(
            android.view.View(this),
            LinearLayout.LayoutParams(1, height)
        )
    }
}
