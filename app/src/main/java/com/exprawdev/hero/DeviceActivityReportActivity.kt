package com.exprawdev.hero

import android.app.Activity
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DeviceActivityReportActivity : Activity() {

    private val backgroundColor = Color.rgb(8, 13, 24)
    private val cardColor = Color.rgb(20, 30, 48)
    private val accentColor = Color.rgb(0, 184, 217)

    private lateinit var reportContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 28, 22, 28)
        }

        val title = TextView(this).apply {
            text = "DEVICE ACTIVITY REPORT"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Daily device usage summary"
            textSize = 13f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }

        val permissionButton = Button(this).apply {
            text = "OPEN USAGE ACCESS SETTINGS"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }

        val refreshButton = Button(this).apply {
            text = "REFRESH REPORT"
            setOnClickListener {
                loadReport()
            }
        }

        reportContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(permissionButton)
        root.addView(refreshButton)
        root.addView(reportContainer)

        scrollView.addView(root)
        setContentView(scrollView)
    }

    override fun onResume() {
        super.onResume()
        if (::reportContainer.isInitialized) {
            loadReport()
        }
    }

    private fun loadReport() {
        reportContainer.removeAllViews()

        if (!hasUsageAccess()) {
            addInfoCard(
                "Permission required",
                "Enable Usage Access for HERO in Android settings."
            )
            return
        }

        val usageManager = getSystemService(
            Context.USAGE_STATS_SERVICE
        ) as UsageStatsManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageList: List<UsageStats> =
            usageManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ) ?: emptyList()

        val filteredUsage = usageList
            .filter {
                it.totalTimeInForeground > 0 &&
                    it.packageName != packageName
            }
            .sortedByDescending {
                it.totalTimeInForeground
            }

        val totalTime = filteredUsage.sumOf {
            it.totalTimeInForeground
        }

        addInfoCard(
            "REPORT DATE",
            SimpleDateFormat(
                "dd MMMM yyyy",
                Locale.getDefault()
            ).format(Date())
        )

        addInfoCard(
            "TOTAL RECORDED APP TIME",
            formatDuration(totalTime)
        )

        addInfoCard(
            "APPLICATIONS USED",
            filteredUsage.size.toString()
        )

        if (filteredUsage.isEmpty()) {
            addInfoCard(
                "NO ACTIVITY DATA",
                "No application usage data was returned for today."
            )
            return
        }

        addSectionTitle("APPLICATION ACTIVITY")

        for (usage in filteredUsage) {
            val appName = getApplicationName(usage.packageName)

            addInfoCard(
                appName,
                "${formatDuration(usage.totalTimeInForeground)}\n" +
                    usage.packageName
            )
        }

        addInfoCard(
            "DATA LIMITATION",
            "Android provides usage statistics, not a complete record " +
                "of everything done inside each application."
        )
    }

    private fun hasUsageAccess(): Boolean {
        val usageManager = getSystemService(
            Context.USAGE_STATS_SERVICE
        ) as UsageStatsManager

        val now = System.currentTimeMillis()

        return usageManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - 60_000,
            now
        )?.isNotEmpty() == true
    }

    private fun getApplicationName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                0
            )

            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            packageName
        }
    }

    private fun formatDuration(milliseconds: Long): String {
        val totalMinutes = milliseconds / 60000
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return "${hours}h ${minutes}m"
    }

    private fun addSectionTitle(text: String) {
        val heading = TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(accentColor)
            setPadding(4, 20, 4, 12)
        }

        reportContainer.addView(heading)
    }

    private fun addInfoCard(
        title: String,
        value: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 14, 16, 14)
            setBackgroundColor(cardColor)
        }

        val heading = TextView(this).apply {
            text = title
            textSize = 12f
            setTextColor(accentColor)
        }

        val detail = TextView(this).apply {
            text = value
            textSize = 15f
            setTextColor(Color.WHITE)
            setPadding(0, 8, 0, 0)
        }

        card.addView(heading)
        card.addView(detail)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        reportContainer.addView(card, params)
    }
}
