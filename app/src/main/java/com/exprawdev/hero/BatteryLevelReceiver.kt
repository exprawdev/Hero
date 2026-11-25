
package com.exprawdev.hero

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class BatteryLevelReceiver : BroadcastReceiver() {

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

        val batteryPercentage = (level * 100) / scale

        val threshold = BatteryAlertActivity.getThreshold(context)

        val preferences = context.getSharedPreferences(
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
