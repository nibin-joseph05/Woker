package com.woker.data.alarm

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.woker.ui.alarm.AlarmRingActivity


object AlarmEnforcer {

    private var handler: Handler? = null
    private var monitorRunnable: Runnable? = null
    private var isEnforcing = false

    fun startEnforcing(context: Context) {
        if (isEnforcing) return

        isEnforcing = true
        handler = Handler(Looper.getMainLooper())

        monitorRunnable = object : Runnable {
            override fun run() {
                if (!isEnforcing || !AlarmService.isRunning) {
                    stopEnforcing()
                    return
                }

                
                if (!isAlarmActivityInForeground(context)) {
                    
                    val intent = Intent(context, AlarmRingActivity::class.java).apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                    Intent.FLAG_ACTIVITY_NO_USER_ACTION
                        )
                    }
                    context.startActivity(intent)
                }

                
                handler?.postDelayed(this, 500)
            }
        }

        handler?.post(monitorRunnable!!)
    }

    fun stopEnforcing() {
        isEnforcing = false
        monitorRunnable?.let { handler?.removeCallbacks(it) }
        handler = null
        monitorRunnable = null
    }

    private fun isAlarmActivityInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTasks = try {
            activityManager.getRunningTasks(1)
        } catch (e: Exception) {
            return true 
        }

        if (runningTasks.isNotEmpty()) {
            val topActivity = runningTasks[0].topActivity
            return topActivity?.className == AlarmRingActivity::class.java.name
        }

        return false
    }
}