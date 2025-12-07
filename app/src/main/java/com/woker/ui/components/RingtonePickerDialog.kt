package com.woker.ui.components

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun RingtonePickerDialog(
    onSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    
    val sounds = remember {
        val ringtoneManager = RingtoneManager(context).apply {
            setType(RingtoneManager.TYPE_ALARM)
        }
        val cursor = ringtoneManager.cursor
        val list = mutableListOf<Pair<String, String>>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = ringtoneManager.getRingtoneUri(cursor.position).toString()
            list.add(title to uri)
        }
        list
    }

    var selectedUri by remember { mutableStateOf<String?>(null) }
    var playingRingtone by remember { mutableStateOf<Ringtone?>(null) }

    
    DisposableEffect(Unit) {
        onDispose {
            playingRingtone?.stop()
        }
    }

    fun playPreview(uri: String) {
        try {
            
            playingRingtone?.stop()

            val ringtone = RingtoneManager.getRingtone(context, Uri.parse(uri))
            playingRingtone = ringtone
            ringtone.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AlertDialog(
        onDismissRequest = {
            playingRingtone?.stop()
            onDismiss()
        },
        title = { Text("Choose Alarm Sound") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp) 
                    .verticalScroll(rememberScrollState())
            ) {
                sounds.forEach { (title, uri) ->
                    Text(
                        text = if (uri == selectedUri) "● $title" else title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedUri = uri
                                playPreview(uri)
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    playingRingtone?.stop()
                    onSelected(selectedUri)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    playingRingtone?.stop()
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
