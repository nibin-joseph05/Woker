package com.woker.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.woker.model.Alarm
import kotlinx.coroutines.flow.map
import android.media.RingtoneManager
import android.net.Uri

private val Context.dataStore by preferencesDataStore("alarms_ds")

object AlarmDataStore {

    private val ALARMS = stringSetPreferencesKey("alarms")

    suspend fun saveAlarm(context: Context, alarm: Alarm) {
        context.dataStore.edit { pref ->
            val updated = pref[ALARMS]?.toMutableSet() ?: mutableSetOf()
            updated.add("${alarm.id}|${alarm.time}|${alarm.enabled}|${alarm.soundUri ?: ""}")
            pref[ALARMS] = updated
        }
    }

    suspend fun deleteAlarm(context: Context, alarm: Alarm) {
        context.dataStore.edit { pref ->
            val updated = pref[ALARMS]?.toMutableSet() ?: mutableSetOf()
            updated.removeIf { it.startsWith("${alarm.id}|") }
            pref[ALARMS] = updated
        }
    }

    suspend fun updateAlarm(context: Context, alarm: Alarm) {
        context.dataStore.edit { pref ->
            val updated = pref[ALARMS]?.toMutableSet() ?: mutableSetOf()
            updated.removeIf { it.startsWith("${alarm.id}|") }
            updated.add("${alarm.id}|${alarm.time}|${alarm.enabled}|${alarm.soundUri ?: ""}")
            pref[ALARMS] = updated
        }
    }

    fun getAlarms(context: Context) = context.dataStore.data.map { pref ->
        pref[ALARMS]?.map { item ->
            val parts = item.split("|")
            Alarm(
                id = parts[0].toLong(),
                time = parts[1],
                enabled = parts[2].toBoolean(),
                soundUri = parts.getOrNull(3)
            )
        } ?: emptyList()
    }

    fun getRingtoneTitle(context: Context, soundUri: String?): String {
        if (soundUri.isNullOrEmpty()) return "Default Sound"
        return try {
            val ringtone = RingtoneManager.getRingtone(context, Uri.parse(soundUri))
            ringtone?.getTitle(context) ?: "Unknown Sound"
        } catch (e: Exception) {
            "Unknown Sound"
        }
    }
}
