
package com.exprawdev.hero

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

object BatteryAlertNotification {

    private const val CHANNEL_ID = "hero_battery_alert_channel"
    private const val NOTIFICATION_ID = 7101

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HERO Battery Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Low battery alerts from HERO"
                enableVibration(true)
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun showLowBatteryNotification(
        context: Context,
        batteryLevel: Int,
        threshold: Int
    ) {
        if (batteryLevel > threshold) return

        if (
            Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        createChannel(context)

        val openIntent = Intent(
            context,
            BatteryAlertActivity::class.java
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            7101,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("HERO: Low Battery")
            .setContentText(
                "Battery is at $batteryLevel%. " +
                    "Your alert threshold is $threshold%."
            )
            .setStyle(
                Notification.BigTextStyle().bigText(
                    "Your device battery is at $batteryLevel%. " +
                        "Please charge your device."
                )
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification(context: Context) {
        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.cancel(NOTIFICATION_ID)
    }
}
