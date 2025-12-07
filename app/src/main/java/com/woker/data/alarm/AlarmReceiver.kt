package com.woker.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import com.woker.ui.alarm.AlarmRingActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sound = intent.getStringExtra("alarmSound")

        
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "woker:alarm_wakelock"
        )
        wl.acquire(10 * 60 * 1000L) 

        
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmSound", sound)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        
        Thread.sleep(100)

        
        val activityIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("alarmSound", sound)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
        }
        context.startActivity(activityIntent)
    }
}