package com.woker.ui.components

import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun AlarmTimePicker(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, pickedHour, pickedMinute ->
            val time = String.format("%02d:%02d", pickedHour, pickedMinute)
            onTimeSelected(time)
        },
        hour,
        minute,
        true
    ).apply {
        setOnDismissListener { onDismiss() }
        show()
    }
}
