package com.woker.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.woker.ui.alarm.AlarmRingActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sound = intent.getStringExtra("alarmSound")

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "woker:alarm"
        )
        wl.acquire(5000)

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmSound", sound)
        }
        context.startForegroundService(serviceIntent)

        val i = Intent(context, AlarmRingActivity::class.java)
        i.putExtra("alarmSound", sound)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(i)
    }
}
