
package com.exprawdev.hero

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class SOSEmergencyActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(20, 30, 48)
    private val dangerColor = Color.rgb(220, 45, 55)

    private lateinit var phoneInput: EditText
    private lateinit var messageInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "HERO EMERGENCY SOS"
            textSize = 25f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Emergency assistance"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 28)
        }

        val phoneLabel = createLabel("Emergency contact phone number")

        phoneInput = EditText(this).apply {
            hint = "Example: +628123456789"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setSingleLine(true)
            setPadding(14, 12, 14, 12)
        }

        val messageLabel = createLabel("Emergency message")

        messageInput = EditText(this).apply {
            setText("SOS! I need emergency assistance. Please contact me.")
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            minLines = 3
            gravity = Gravity.TOP
            setPadding(14, 12, 14, 12)
        }

        val sosButton = Button(this).apply {
            text = "SEND SOS"
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(dangerColor)
            setOnClickListener {
                confirmEmergency()
            }
        }

        val information = TextView(this).apply {
            text = "SOS is not sent automatically. Review the message and send it yourself in your SMS application."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(12, 20, 12, 20)
        }

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(phoneLabel)
        root.addView(phoneInput)
        root.addView(messageLabel)
        root.addView(messageInput)

        root.addView(
            sosButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                64
            ).apply {
                topMargin = 28
            }
        )

        root.addView(information)
        root.addView(backButton)

        setContentView(root)
    }

    private fun createLabel(value: String): TextView {
        return TextView(this).apply {
            text = value
            textSize = 13f
            setTextColor(Color.WHITE)
            setPadding(0, 14, 0, 8)
        }
    }

    private fun confirmEmergency() {
        val phone = phoneInput.text.toString().trim()
        val message = messageInput.text.toString().trim()

        if (phone.isBlank()) {
            Toast.makeText(
                this,
                "Enter an emergency contact number",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (message.isBlank()) {
            Toast.makeText(
                this,
                "Enter an emergency message",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm SOS")
            .setMessage(
                "Open your SMS application with this emergency message?"
            )
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("CONTINUE") { _, _ ->
                openSms(phone, message)
            }
            .show()
    }

    private fun openSms(phone: String, message: String) {
        try {
            val intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("smsto:${Uri.encode(phone)}")
            ).apply {
                putExtra("sms_body", message)
            }

            startActivity(intent)

        } catch (exception: Exception) {
            Toast.makeText(
                this,
                "No SMS application is available",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
