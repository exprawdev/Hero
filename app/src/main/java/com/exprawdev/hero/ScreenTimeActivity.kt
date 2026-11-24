
package com.exprawdev.hero

import android.app.Activity
import android.app.AppOpsManager
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
import android.widget.Toast
import java.util.Calendar

class ScreenTimeActivity : Activity() {

    private lateinit var usageContainer: LinearLayout
    private lateinit var totalText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }

        val title = TextView(this).apply {
            text = "HERO SCREEN TIME"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 20)
        }

        totalText = TextView(this).apply {
            text = "Today's screen time: --"
            textSize = 17f
            setTextColor(Color.rgb(0, 184, 217))
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 20)
        }

        val permissionButton = Button(this).apply {
            text = "ENABLE USAGE ACCESS"
            setOnClickListener {
                startActivity(
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                )
            }
        }

        val refreshButton = Button(this).apply {
            text = "REFRESH SCREEN TIME"
            setOnClickListener {
                loadUsageData()
            }
        }

        val scrollView = ScrollView(this)

        usageContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scrollView.addView(usageContainer)

        val backButton = Button(this).apply {
            text = "BACK"
            setOnClickListener {
                finish()
            }
        }

        root.addView(title)
        root.addView(totalText)
        root.addView(permissionButton)
        root.addView(refreshButton)

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

    override fun onResume() {
        super.onResume()
        loadUsageData()
    }

    private fun hasUsagePermission(): Boolean {
        val appOps = getSystemService(
            Context.APP_OPS_SERVICE
        ) as AppOpsManager

        @Suppress("DEPRECATION")
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )

        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadUsageData() {
        usageContainer.removeAllViews()

        if (!hasUsagePermission()) {
            totalText.text = "Usage Access permission required"

            addUsageText(
                "Enable Usage Access to view application usage."
            )

            return
        }

        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE)
                as android.app.usage.UsageStatsManager

        val calendar = Calendar.getInstance()

        calendar.set(
            Calendar.HOUR_OF_DAY,
            0
        )

        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageList = usageStatsManager.queryUsageStats(
            android.app.usage.UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        if (usageList.isNullOrEmpty()) {
            totalText.text = "Today's screen time: 0 minutes"
            addUsageText("No application usage data available yet.")
            return
        }

        val filteredUsage = usageList
            .filter { it.totalTimeInForeground > 0L }
            .sortedByDescending { it.totalTimeInForeground }

        val totalMillis = filteredUsage.sumOf {
            it.totalTimeInForeground
        }

        totalText.text = "Today's screen time: ${formatDuration(totalMillis)}"

        for (usage in filteredUsage) {
            val appName = try {
                val appInfo = packageManager.getApplicationInfo(
                    usage.packageName,
                    0
                )

                packageManager.getApplicationLabel(appInfo).toString()
            } catch (exception: Exception) {
                usage.packageName
            }

            addUsageText(
                "$appName\n${formatDuration(usage.totalTimeInForeground)}"
            )
        }
    }

    private fun formatDuration(milliseconds: Long): String {
        val totalMinutes = milliseconds / 60000L
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }

    private fun addUsageText(message: String) {
        val item = TextView(this).apply {
            text = message
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.rgb(20, 30, 48))
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 10
        }

        usageContainer.addView(item, params)
    }
}
