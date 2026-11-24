package com.exprawdev.hero

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PairingConsentActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DEVICE_NAME = "device_name"
        const val EXTRA_REQUEST_ID = "request_id"

        const val RESULT_APPROVED = "approved"
        const val RESULT_DECLINED = "declined"
        const val EXTRA_CONSENT_RESULT = "consent_result"
    }

    private val background = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(19, 28, 45)
    private val blue = Color.rgb(23, 105, 255)
    private val white = Color.WHITE
    private val muted = Color.LTGRAY
    private val red = Color.rgb(220, 65, 65)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = background
        window.navigationBarColor = background

        val deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME)
            ?: "Unknown device"

        val requestId = intent.getStringExtra(EXTRA_REQUEST_ID)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 32)
            setBackgroundColor(background)
        }

        val title = TextView(this).apply {
            text = "PAIRING REQUEST"
            textSize = 25f
            setTextColor(blue)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val deviceCard = TextView(this).apply {
            text = "Device requesting connection:\n\n$deviceName"
            textSize = 17f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(20, 24, 20, 24)
            setBackgroundColor(cardColor)
        }

        val warning = TextView(this).apply {
            text = "Review this request carefully. Only approve a device you recognize and trust. You can decline at any time."
            textSize = 14f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(8, 24, 8, 24)
        }

        val approveButton = Button(this).apply {
            text = "APPROVE REQUEST"
            setTextColor(white)
            setBackgroundColor(blue)
        }

        val declineButton = Button(this).apply {
            text = "DECLINE REQUEST"
            setTextColor(white)
            setBackgroundColor(red)
        }

        approveButton.setOnClickListener {
            returnConsent(RESULT_APPROVED, requestId)
        }

        declineButton.setOnClickListener {
            returnConsent(RESULT_DECLINED, requestId)
        }

        root.addView(title)
        addSpace(root, 24)
        root.addView(deviceCard)
        root.addView(warning)
        root.addView(approveButton)
        addSpace(root, 12)
        root.addView(declineButton)

        setContentView(root)
    }

    private fun returnConsent(
        result: String,
        requestId: String?
    ) {
        val resultIntent = android.content.Intent().apply {
            putExtra(EXTRA_CONSENT_RESULT, result)
            putExtra(EXTRA_REQUEST_ID, requestId)
        }

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
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
