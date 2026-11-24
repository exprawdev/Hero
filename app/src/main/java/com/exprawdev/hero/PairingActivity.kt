
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

        val role = DeviceRoleStorage(this).getRole()
        val roleName = when (role) {
            DeviceRole.PARENT -> "PARENT DEVICE"
            DeviceRole.CHILD -> "CHILD DEVICE"
            null -> "ROLE NOT SELECTED"
        }

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

        val roleDisplay = TextView(this).apply {
            text = roleName
            textSize = 15f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(16, 18, 16, 18)
            setBackgroundColor(cardColor)
        }

        val description = TextView(this).apply {
            text = "Create a code on one device and enter it on the other."
            textSize = 14f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 24)
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
        }

        val verifyButton = createButton("CHECK CODE")

        generateButton.setOnClickListener {
            if (role == null) {
                showMessage("Please select a device role first.")
            } else {
                codeDisplay.text = PairingCode.generate()
                showMessage("Local test code generated. Server pairing is not connected yet.")
            }
        }

        verifyButton.setOnClickListener {
            val code = codeInput.text.toString().trim()

            if (PairingCode.isValid(code)) {
                showMessage("Code format is valid. Server verification is not connected yet.")
            } else {
                showMessage("Please enter a valid 6-digit code.")
            }
        }

        root.addView(title)
        addSpace(root, 18)
        root.addView(roleDisplay)
        addSpace(root, 16)
        root.addView(description)
        root.addView(codeDisplay)
        addSpace(root, 18)
        root.addView(generateButton)
        addSpace(root, 18)
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
        }
    }

    private fun addSpace(parent: LinearLayout, height: Int) {
        parent.addView(
            android.view.View(this),
            LinearLayout.LayoutParams(1, height)
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
