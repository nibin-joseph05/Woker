package com.woker.data.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.*
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.woker.R

class AlarmService : Service() {

    private var player: MediaPlayer? = null
    private lateinit var audio: AudioManager
    private lateinit var volumeListener: AudioManager.OnAudioFocusChangeListener

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val soundUri = intent?.getStringExtra("alarmSound")
        val uri = if (!soundUri.isNullOrEmpty()) Uri.parse(soundUri)
        else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        
        audio.mode = AudioManager.MODE_IN_COMMUNICATION
        audio.ringerMode = AudioManager.RINGER_MODE_NORMAL
        audio.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audio.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
        )

        
        volumeListener = AudioManager.OnAudioFocusChangeListener {
            audio.setStreamVolume(
                AudioManager.STREAM_ALARM,
                audio.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                0
            )
        }
        audio.requestAudioFocus(
            volumeListener,
            AudioManager.STREAM_ALARM,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )


        player = MediaPlayer().apply {
            setDataSource(this@AlarmService, uri)

            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )

            setOnPreparedListener { it.start() }
            setOnErrorListener { _, _, _ -> false }

            isLooping = true
            prepareAsync()
        }



        val notification = NotificationCompat.Builder(this, "alarm")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alarm ringing")
            .setContentText("Solve the problem to stop")
            .setOngoing(true)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    fun stopAlarm() {
        
        player?.stop()
        player?.release()
        player = null

        
        try {
            audio.abandonAudioFocus(volumeListener)
        } catch (_: Exception) {}

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
