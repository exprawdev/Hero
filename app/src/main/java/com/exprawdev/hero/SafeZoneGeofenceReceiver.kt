
package com.exprawdev.hero

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import org.json.JSONArray
import org.json.JSONObject

class SafeZoneGeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return

        if (event.hasError()) {
            return
        }

        val transition = event.geofenceTransition

        val message = when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER ->
                "The device entered the safe zone"

            Geofence.GEOFENCE_TRANSITION_EXIT ->
                "The device left the safe zone"

            else -> return
        }

        saveEvent(context, message)
        showNotification(context, message)
    }

    private fun saveEvent(context: Context, message: String) {
        val preferences = context.getSharedPreferences(
            "hero_safe_zone_history",
            Context.MODE_PRIVATE
        )

        val records = try {
            JSONArray(
                preferences.getString("records", "[]") ?: "[]"
            )
        } catch (_: Exception) {
            JSONArray()
        }

        val record = JSONObject().apply {
            put("event", message)
            put("timestamp", System.currentTimeMillis())
        }

        records.put(record)

        val limitedRecords = JSONArray()
        val startIndex = (records.length() - 100).coerceAtLeast(0)

        for (index in startIndex until records.length()) {
            limitedRecords.put(records.getJSONObject(index))
        }

        preferences.edit()
            .putString("records", limitedRecords.toString())
            .apply()
    }

    private fun showNotification(
        context: Context,
        message: String
    ) {
        val channelId = "hero_safe_zone_channel"

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Safe Zone Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )

            manager.createNotificationChannel(channel)
        }

        if (
            Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openIntent = Intent(
            context,
            SafeZoneActivity::class.java
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            9101,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("HERO Safe Zone")
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(
            9101,
            notification
        )
    }
}
