package com.woker.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.woker.data.datastore.AlarmDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            val alarms = AlarmDataStore.getAlarms(context).first()
            alarms.filter { it.enabled }.forEach {
                AlarmScheduler.schedule(context, it)
            }
        }
    }
}
