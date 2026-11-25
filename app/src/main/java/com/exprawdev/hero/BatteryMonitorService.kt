
package com.exprawdev.hero

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder

class BatteryMonitorService : Service() {

    companion object {
        const val ACTION_START =
            "com.exprawdev.hero.action.START_BATTERY_MONITOR"

        const val ACTION_STOP =
            "com.exprawdev.hero.action.STOP_BATTERY_MONITOR"

        private const val CHANNEL_ID =
            "hero_battery_monitor_channel"

        private const val NOTIFICATION_ID = 7301
    }

    private var receiverRegistered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != Intent.ACTION_BATTERY_CHANGED) {
                return
            }

            val level = intent.getIntExtra(
                BatteryManager.EXTRA_LEVEL,
                -1
            )

            val scale = intent.getIntExtra(
                BatteryManager.EXTRA_SCALE,
                -1
            )

            if (level < 0 || scale <= 0) return

            val batteryPercentage = level * 100 / scale

            val threshold =
                BatteryAlertActivity.getThreshold(context)

            val preferences = getSharedPreferences(
                "hero_battery_alert",
                Context.MODE_PRIVATE
            )

            val alertsEnabled = preferences.getBoolean(
                "alerts_enabled",
                true
            )

            if (alertsEnabled && batteryPercentage <= threshold) {
                BatteryAlertNotification.showLowBatteryNotification(
                    context,
                    batteryPercentage,
                    threshold
                )
            } else {
                BatteryAlertNotification.cancelNotification(context)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {
            ACTION_STOP -> {
                stopMonitoring()
                return START_NOT_STICKY
            }

            ACTION_START, null -> {
                startForeground(
                    NOTIFICATION_ID,
                    createForegroundNotification()
                )

                registerBatteryReceiver()
            }
        }

        return START_STICKY
    }

    private fun registerBatteryReceiver() {
        if (receiverRegistered) return

        val filter = IntentFilter(
            Intent.ACTION_BATTERY_CHANGED
        )

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(
                batteryReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(batteryReceiver, filter)
        }

        receiverRegistered = true
    }

    private fun stopMonitoring() {
        if (receiverRegistered) {
            unregisterReceiver(batteryReceiver)
            receiverRegistered = false
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HERO Battery Monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description =
                    "Shows when HERO battery monitoring is active"
            }

            val manager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                .setContentTitle("HERO Battery Monitor")
                .setContentText("Battery monitoring is active")
                .setOngoing(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                .setContentTitle("HERO Battery Monitor")
                .setContentText("Battery monitoring is active")
                .setOngoing(true)
                .build()
        }
    }

    override fun onDestroy() {
        if (receiverRegistered) {
            unregisterReceiver(batteryReceiver)
            receiverRegistered = false
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
