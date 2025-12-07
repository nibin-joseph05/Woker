package com.woker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.woker.data.alarm.AlarmScheduler
import com.woker.data.datastore.AlarmDataStore
import com.woker.model.Alarm
import com.woker.ui.components.AlarmItem
import com.woker.ui.components.AlarmTimePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }
    var alarmToEdit by remember { mutableStateOf<Alarm?>(null) }

    val alarms by AlarmDataStore.getAlarms(context).collectAsState(initial = emptyList())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Woker",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    alarmToEdit = null
                    showTimePicker = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "+",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (alarms.isEmpty()) {
                    Text(
                        text = "⏰ No alarms yet\nTap + to add your first alarm",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(alarms) { alarm ->
                            AlarmItem(
                                alarm = alarm,
                                onEdit = {
                                    alarmToEdit = it
                                    showTimePicker = true
                                },
                                onDelete = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        AlarmDataStore.deleteAlarm(context, it)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Text(
                text = "Developed & Designed by Nibin",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showTimePicker) {
        AlarmTimePicker(
            initialTime = alarmToEdit?.time,
            initialSound = alarmToEdit?.soundUri,
            onAlarmReady = { time, sound ->
                val updatedAlarm = if (alarmToEdit == null)
                    Alarm(time = time, soundUri = sound)
                else
                    alarmToEdit!!.copy(time = time, soundUri = sound)

                CoroutineScope(Dispatchers.IO).launch {
                    if (alarmToEdit == null) AlarmDataStore.saveAlarm(context, updatedAlarm)
                    else AlarmDataStore.updateAlarm(context, updatedAlarm)

                    AlarmScheduler.schedule(context, updatedAlarm)
                }


                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}
