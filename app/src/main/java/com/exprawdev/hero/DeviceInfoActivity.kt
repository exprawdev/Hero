package com.exprawdev.hero

import android.app.Activity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class DeviceInfoActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceIdentity = DeviceIdentity(this)
        val deviceId = deviceIdentity.getDeviceId()
        val role = DeviceRoleStorage(this).getRole()?.name ?: "NOT SELECTED"

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "DEVICE INFORMATION"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val roleText = TextView(this).apply {
            text = "Device Role: $role"
            textSize = 17f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 20)
        }

        val idTitle = TextView(this).apply {
            text = "HERO DEVICE ID"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        val idText = TextView(this).apply {
            text = deviceId
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 24)
        }

        val copyButton = Button(this).apply {
            text = "COPY DEVICE ID"
            setOnClickListener {
                val clipboard = getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

                clipboard.setPrimaryClip(
                    ClipData.newPlainText("HERO Device ID", deviceId)
                )

                Toast.makeText(
                    this@DeviceInfoActivity,
                    "Device ID copied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        layout.addView(title)
        layout.addView(roleText)
        layout.addView(idTitle)
        layout.addView(idText)
        layout.addView(copyButton)
        layout.addView(backButton)

        setContentView(layout)
    }
}
