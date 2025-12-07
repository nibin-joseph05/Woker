package com.woker.ui.alarm

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.woker.data.alarm.AlarmService
import com.woker.ui.theme.WokerTheme

class AlarmRingActivity : ComponentActivity() {

    private var alarmService: AlarmService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            alarmService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
        setupLockdownMode()

        setContent {
            WokerTheme {
                AlarmRingScreen(
                    onSolved = {
                        stopAlarmAndFinish()
                    }
                )
            }
        }
    }

    private fun setupLockdownMode() {
        
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                
            }
        })

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun stopAlarmAndFinish() {
        
        val intent = Intent(this, AlarmService::class.java)
        stopService(intent)

        
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        
        if (AlarmService.isRunning) {
            val intent = Intent(this, AlarmRingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        
        if (AlarmService.isRunning) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if (AlarmService.isRunning) {
                    val intent = Intent(this, AlarmRingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                }
            }, 100)
        }
    }

    override fun onStop() {
        super.onStop()
        
        if (AlarmService.isRunning) {
            val intent = Intent(this, AlarmRingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        super.onDestroy()
    }

    
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    }
}