
package com.exprawdev.hero

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class SafetySettingsActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(20, 30, 48)
    private val accentColor = Color.rgb(0, 184, 217)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "HERO SAFETY SETTINGS"
            textSize = 23f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 8)
        }

        val subtitle = TextView(this).apply {
            text = "Device and family security controls"
            textSize = 13f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }

        val scrollView = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        addSection(content, "NOTIFICATION SETTINGS")
        addSettingButton(
            content,
            "APP NOTIFICATION SETTINGS",
            "Manage HERO notification permissions"
        ) {
            openAppNotificationSettings()
        }

        addSection(content, "DO NOT DISTURB")
        addSettingButton(
            content,
            "DO NOT DISTURB SETTINGS",
            "Configure quiet hours in Android settings"
        ) {
            openDoNotDisturbSettings()
        }

        addSection(content, "DEVICE PERMISSIONS")
        addSettingButton(
            content,
            "APP PERMISSIONS",
            "Review camera, microphone and location permissions"
        ) {
            openAppPermissionSettings()
        }

        addSettingButton(
            content,
            "LOCATION SETTINGS",
            "Manage device location access"
        ) {
            startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            )
        }

        addSection(content, "FAMILY SECURITY")

        addInformation(
            content,
            "Transparent monitoring",
            "Monitoring features must be visible and use Android permissions."
        )

        addInformation(
            content,
            "Parent and child connection",
            "Family device synchronization requires a secure backend and verified pairing."
        )

        addInformation(
            content,
            "Emergency protection",
            "Emergency and SOS features will be added in a later development step."
        )

        scrollView.addView(content)

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(subtitle)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(backButton)

        setContentView(root)
    }

    private fun addSection(
        parent: LinearLayout,
        title: String
    ) {
        val section = TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(accentColor)
            setPadding(8, 20, 8, 10)
        }

        parent.addView(section)
    }

    private fun addSettingButton(
        parent: LinearLayout,
        title: String,
        description: String,
        action: () -> Unit
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 14, 16, 14)
            setBackgroundColor(cardColor)
        }

        val button = Button(this).apply {
            text = title
            setTextColor(Color.WHITE)
            setOnClickListener {
                action()
            }
        }

        val detail = TextView(this).apply {
            text = description
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        card.addView(button)
        card.addView(detail)

        parent.addView(
            card,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        )
    }

    private fun addInformation(
        parent: LinearLayout,
        title: String,
        description: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(cardColor)
        }

        val heading = TextView(this).apply {
            text = title
            textSize = 15f
            setTextColor(Color.WHITE)
        }

        val detail = TextView(this).apply {
            text = description
            textSize = 12f
            setTextColor(Color.LTGRAY)
            setPadding(0, 8, 0, 0)
        }

        card.addView(heading)
        card.addView(detail)

        parent.addView(
            card,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        )
    }

    private fun openAppNotificationSettings() {
        val intent = Intent(
            Settings.ACTION_APP_NOTIFICATION_SETTINGS
        ).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }

        startActivity(intent)
    }

    private fun openDoNotDisturbSettings() {
        startActivity(
            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        )
    }

    private fun openAppPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )

        startActivity(intent)
    }
}
