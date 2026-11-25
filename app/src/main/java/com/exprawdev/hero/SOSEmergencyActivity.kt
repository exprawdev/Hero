
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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class SOSEmergencyActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(20, 30, 48)
    private val accentColor = Color.rgb(0, 184, 217)

    private lateinit var phoneInput: EditText
    private lateinit var messageInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 24)
        }

        val title = TextView(this).apply {
            text = "SOS EMERGENCY"
            textSize = 25f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Prepare an emergency message"
            textSize = 13f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 26)
        }

        val phoneLabel = createLabel("DESTINATION PHONE NUMBER")

        phoneInput = EditText(this).apply {
            hint = "Enter phone number"
            textSize = 16f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setSingleLine(true)
            setPadding(16, 14, 16, 14)
            setBackgroundColor(cardColor)
        }

        val messageLabel = createLabel("EMERGENCY MESSAGE")

        messageInput = EditText(this).apply {
            hint = "Enter your emergency message"
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            gravity = Gravity.TOP
            minLines = 5
            setPadding(16, 14, 16, 14)
            setBackgroundColor(cardColor)
            setText(
                "I need help. Please contact me as soon as possible."
            )
        }

        val confirmButton = Button(this).apply {
            text = "PREPARE SOS MESSAGE"
            setOnClickListener {
                confirmEmergencyMessage()
            }
        }

        val historyButton = Button(this).apply {
            text = "VIEW SOS HISTORY"
            setOnClickListener {
                startActivity(
                    Intent(
                        this@SOSEmergencyActivity,
                        SOSHistoryActivity::class.java
                    )
                )
            }
        }

        val note = TextView(this).apply {
            text = "HERO saves the prepared message locally and opens " +
                "your SMS application. You must review and send the " +
                "message yourself. HERO does not send SMS automatically."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            setPadding(4, 20, 4, 8)
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(phoneLabel)
        root.addView(phoneInput)
        addSpacing(root, 18)
        root.addView(messageLabel)
        root.addView(messageInput)
        addSpacing(root, 20)
        root.addView(confirmButton)
        root.addView(historyButton)
        root.addView(note)

        scrollView.addView(root)
        setContentView(scrollView)
    }

    private fun confirmEmergencyMessage() {
        val phoneNumber = phoneInput.text.toString().trim()
        val message = messageInput.text.toString().trim()

        if (phoneNumber.isBlank()) {
            phoneInput.error = "Enter a phone number"
            return
        }

        if (phoneNumber.length < 5) {
            phoneInput.error = "Enter a valid phone number"
            return
        }

        if (message.isBlank()) {
            messageInput.error = "Enter an emergency message"
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm SOS Message")
            .setMessage(
                "Destination: $phoneNumber\n\n" +
                    "Message:\n$message\n\n" +
                    "Save this message to SOS History and open your SMS application?"
            )
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("CONFIRM") { _, _ ->

                SOSHistoryActivity.recordPreparedMessage(
                    this,
                    phoneNumber,
                    message
                )

                openSmsApplication(phoneNumber, message)
            }
            .show()
    }

    private fun openSmsApplication(
        phoneNumber: String,
        message: String
    ) {
        val smsIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("smsto:${Uri.encode(phoneNumber)}")
        ).apply {
            putExtra("sms_body", message)
        }

        try {
            startActivity(smsIntent)
        } catch (_: Exception) {
            Toast.makeText(
                this,
                "No compatible SMS application was found.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun createLabel(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 12f
            setTextColor(accentColor)
            setPadding(4, 8, 4, 8)
        }
    }

    private fun addSpacing(
        parent: LinearLayout,
        height: Int
    ) {
        parent.addView(
            TextView(this),
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            )
        )
    }
}
