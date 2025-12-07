package com.woker.ui.alarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.woker.data.alarm.AlarmService
import com.woker.ui.theme.WokerTheme

class AlarmRingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })

        setContent {
            WokerTheme {
                AlarmRingScreen(
                    onSolved = {
                        stopAlarmSound()
                        finish()
                    }
                )
            }
        }
    }

    private fun stopAlarmSound() {
        val i = Intent(this, AlarmService::class.java)
        stopService(i)
    }
}
