
package com.exprawdev.hero

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class AppManagementActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(20, 30, 48)
    private val accentColor = Color.rgb(0, 184, 217)

    private lateinit var appContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "HERO APP MANAGEMENT"
            textSize = 23f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 8)
        }

        val subtitle = TextView(this).apply {
            text = "Installed applications on this device"
            textSize = 13f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }

        val scrollView = ScrollView(this)

        appContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scrollView.addView(appContainer)

        val refreshButton = Button(this).apply {
            text = "REFRESH APPLICATIONS"
            setOnClickListener {
                loadInstalledApps()
            }
        }

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

        root.addView(refreshButton)
        root.addView(backButton)

        setContentView(root)

        loadInstalledApps()
    }

    @Suppress("DEPRECATION")
    private fun loadInstalledApps() {
        appContainer.removeAllViews()

        val installedApps = packageManager
            .getInstalledApplications(0)
            .sortedBy {
                packageManager.getApplicationLabel(it).toString()
                    .lowercase()
            }

        val countText = TextView(this).apply {
            text = "${installedApps.size} applications found"
            textSize = 15f
            setTextColor(accentColor)
            setPadding(8, 12, 8, 16)
        }

        appContainer.addView(countText)

        for (app in installedApps) {
            val appName = packageManager
                .getApplicationLabel(app)
                .toString()

            addApplicationCard(appName, app)
        }
    }

    private fun addApplicationCard(
        appName: String,
        appInfo: ApplicationInfo
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 14, 16, 14)
            setBackgroundColor(cardColor)
        }

        val name = TextView(this).apply {
            text = appName
            textSize = 16f
            setTextColor(Color.WHITE)
        }

        val packageName = TextView(this).apply {
            text = appInfo.packageName
            textSize = 11f
            setTextColor(Color.LTGRAY)
            setPadding(0, 6, 0, 8)
        }

        val type = if (
            appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        ) {
            "System application"
        } else {
            "User-installed application"
        }

        val typeText = TextView(this).apply {
            text = type
            textSize = 12f
            setTextColor(accentColor)
        }

        val detailsButton = Button(this).apply {
            text = "APP DETAILS"
            setOnClickListener {
                openApplicationSettings(appInfo.packageName)
            }
        }

        card.addView(name)
        card.addView(packageName)
        card.addView(typeText)
        card.addView(detailsButton)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        appContainer.addView(card, params)
    }

    private fun openApplicationSettings(packageName: String) {
        val intent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )

        try {
            startActivity(intent)
        } catch (exception: Exception) {
            Toast.makeText(
                this,
                "Unable to open application settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
