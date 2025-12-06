package com.woker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.woker.ui.components.AlarmItem
import com.woker.ui.components.AlarmTimePicker
import java.util.Calendar

@Composable
fun HomeScreen() {
    var alarms by remember { mutableStateOf(listOf<String>()) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showTimePicker = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (alarms.isEmpty()) {
                Text(
                    text = "No alarms yet. Tap + to add one ⏰",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                    alarms.forEach { time ->
                        AlarmItem(time)
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        AlarmTimePicker(
            onTimeSelected = { selectedTime ->
                alarms = alarms + selectedTime
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}
