
package com.exprawdev.hero

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class DashboardActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(20, 30, 48)
    private val accentColor = Color.rgb(0, 184, 217)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "HERO"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "FAMILY SAFETY DASHBOARD"
            textSize = 13f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 28)
        }

        root.addView(title)
        root.addView(subtitle)

        addDashboardButton(root, "SOS EMERGENCY", "Send an emergency message") {
            startActivity(Intent(this, SOSEmergencyActivity::class.java))
        }

        addDashboardButton(root, "DEVICE PAIRING", "Connect parent and child devices") {
            startActivity(Intent(this, RoleSelectionActivity::class.java))
        }

        addDashboardButton(root, "DEVICE INFORMATION", "View device ID and device role") {
            startActivity(Intent(this, DeviceInfoActivity::class.java))
        }

        addDashboardButton(root, "DEVICE STATUS", "Battery, internet and Android version") {
            startActivity(Intent(this, DeviceStatusActivity::class.java))
        }

        addDashboardButton(root, "BATTERY MONITOR", "View battery level and charging details") {
            startActivity(Intent(this, BatteryMonitorActivity::class.java))
        }

        addDashboardButton(root, "LOCATION", "View this device's GPS location") {
            startActivity(Intent(this, LocationActivity::class.java))
        }

        addDashboardButton(root, "LOCATION HISTORY", "View saved GPS location records") {
            startActivity(Intent(this, LocationHistoryActivity::class.java))
        }

        addDashboardButton(root, "SAFE ZONE", "Configure your family's safe area") {
            startActivity(Intent(this, SafeZoneActivity::class.java))
        }

        addDashboardButton(root, "SCREEN TIME", "View daily application usage") {
            startActivity(Intent(this, ScreenTimeActivity::class.java))
        }

        addDashboardButton(root, "DEVICE ACTIVITY REPORT", "View daily application activity") {
            startActivity(Intent(this, DeviceActivityReportActivity::class.java))
        }

        addDashboardButton(root, "APP MANAGEMENT", "View installed applications") {
            startActivity(Intent(this, AppManagementActivity::class.java))
        }

        addDashboardButton(root, "SAFETY SETTINGS", "Manage device and family security") {
            startActivity(Intent(this, SafetySettingsActivity::class.java))
        }

        scrollView.addView(root)
        setContentView(scrollView)
    }

    private fun addDashboardButton(
        parent: LinearLayout,
        title: String,
        description: String,
        action: () -> Unit
    ) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 14, 18, 14)
            setBackgroundColor(cardColor)
        }

        val button = Button(this).apply {
            text = title
            textSize = 15f
            setTextColor(Color.WHITE)
            setOnClickListener { action() }
        }

        val detail = TextView(this).apply {
            text = description
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        container.addView(button)
        container.addView(detail)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 14
        }

        parent.addView(container, params)
    }
}
