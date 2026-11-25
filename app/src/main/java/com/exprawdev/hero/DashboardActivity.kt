
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 32)
        }

        val title = TextView(this).apply {
            text = "HERO DASHBOARD"
            textSize = 26f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }

        layout.addView(title)

        addDashboardButton(layout, "SOS EMERGENCY") {
            openActivity(SOSEmergencyActivity::class.java)
        }

        addDashboardButton(layout, "SOS HISTORY") {
            openActivity(SOSHistoryActivity::class.java)
        }

        addDashboardButton(layout, "DEVICE PAIRING") {
            openActivity(RoleSelectionActivity::class.java)
        }

        addDashboardButton(layout, "DEVICE INFORMATION") {
            openActivity(DeviceInfoActivity::class.java)
        }

        addDashboardButton(layout, "DEVICE STATUS") {
            openActivity(DeviceStatusActivity::class.java)
        }

        addDashboardButton(layout, "BATTERY MONITOR") {
            openActivity(BatteryMonitorActivity::class.java)
        }

        addDashboardButton(layout, "BATTERY ALERT") {
            openActivity(BatteryAlertActivity::class.java)
        }

        addDashboardButton(layout, "BATTERY MONITOR CONTROL") {
            openActivity(BatteryMonitorControlActivity::class.java)
        }

        addDashboardButton(layout, "BATTERY MONITOR STATUS") {
            openActivity(BatteryMonitorStatusActivity::class.java)
        }

        addDashboardButton(layout, "BATTERY ALERT HISTORY") {
            openActivity(BatteryAlertHistoryActivity::class.java)
        }

        addDashboardButton(layout, "BATTERY ALERT SUMMARY") {
            openActivity(BatteryAlertSummaryActivity::class.java)
        }

        addDashboardButton(layout, "INTERNET MONITOR") {
            openActivity(InternetConnectionActivity::class.java)
        }

        addDashboardButton(layout, "LOCATION") {
            openActivity(LocationActivity::class.java)
        }

        addDashboardButton(layout, "LOCATION HISTORY") {
            openActivity(LocationHistoryActivity::class.java)
        }

        addDashboardButton(layout, "SAFE ZONE") {
            openActivity(SafeZoneActivity::class.java)
        }

        addDashboardButton(layout, "SAFE ZONE HISTORY") {
            openActivity(SafeZoneHistoryActivity::class.java)
        }

        addDashboardButton(layout, "SCREEN TIME") {
            openActivity(ScreenTimeActivity::class.java)
        }

        addDashboardButton(layout, "DEVICE ACTIVITY REPORT") {
            openActivity(DeviceActivityReportActivity::class.java)
        }

        addDashboardButton(layout, "APP MANAGEMENT") {
            openActivity(AppManagementActivity::class.java)
        }

        addDashboardButton(layout, "SAFETY SETTINGS") {
            openActivity(SafetySettingsActivity::class.java)
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun addDashboardButton(
        layout: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            textSize = 14f
            isAllCaps = true

            setOnClickListener {
                action()
            }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            58
        )

        params.setMargins(0, 6, 0, 6)
        layout.addView(button, params)
    }

    private fun openActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }
}
