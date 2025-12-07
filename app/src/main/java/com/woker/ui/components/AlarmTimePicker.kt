package com.woker.ui.components

import android.app.TimePickerDialog
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun AlarmTimePicker(
    initialTime: String? = null,
    initialSound: String? = null,
    onAlarmReady: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedSoundUri by remember { mutableStateOf(initialSound) }
    var showRingtonePicker by remember { mutableStateOf(false) }

    /** Show ringtone picker safely */
    if (showRingtonePicker) {
        RingtonePickerDialog(
            onSelected = { uri ->
                selectedSoundUri = uri
                showRingtonePicker = false
                onAlarmReady(selectedTime!!, selectedSoundUri)
            },
            onDismiss = {
                showRingtonePicker = false
                onAlarmReady(selectedTime!!, selectedSoundUri)
            }
        )
    }

    /** Launch time picker WITHOUT recomposition risk */
    DisposableEffect(Unit) {
        val (hour, minute) = run {
            if (initialTime != null) {
                val parts = initialTime.split(" ", ":")
                val h = parts[0].toInt()
                val m = parts[1].toInt()
                val amPm = parts[2]
                val hour24 = if (amPm == "PM" && h != 12) h + 12 else if (amPm == "AM" && h == 12) 0 else h
                hour24 to m
            } else 6 to 0
        }

        val dialog = TimePickerDialog(
            context,
            { _, pickedHour, pickedMinute ->
                val amPm = if (pickedHour >= 12) "PM" else "AM"
                val hour12 = when {
                    pickedHour == 0 -> 12
                    pickedHour > 12 -> pickedHour - 12
                    else -> pickedHour
                }
                selectedTime = String.format("%02d:%02d %s", hour12, pickedMinute, amPm)

                showRingtonePicker = true
            },
            hour,
            minute,
            false
        )

        dialog.setOnDismissListener {
            if (!showRingtonePicker) onDismiss()
        }

        dialog.show()

        onDispose {
            dialog.dismiss()
        }
    }
}



