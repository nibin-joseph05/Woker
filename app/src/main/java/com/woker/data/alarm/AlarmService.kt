package com.woker.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.*
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.woker.R

class AlarmService : Service() {

    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private lateinit var audio: AudioManager
    private lateinit var volumeListener: AudioManager.OnAudioFocusChangeListener
    private var originalVolume: Int = 0
    private var originalRingerMode: Int = AudioManager.RINGER_MODE_NORMAL

    companion object {
        private const val CHANNEL_ID = "alarm_channel"
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val soundUri = intent?.getStringExtra("alarmSound")
        val uri = if (!soundUri.isNullOrEmpty()) Uri.parse(soundUri)
        else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        isRunning = true
        AlarmEnforcer.startEnforcing(this)

        
        originalVolume = audio.getStreamVolume(AudioManager.STREAM_ALARM)
        originalRingerMode = audio.ringerMode

        
        overrideVolumeSettings()
        startMaxVolumeAlarm(uri)
        startVibration()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alarm Ringing")
            .setContentText("Solve the problem to stop the alarm")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
                setSound(null, null)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun overrideVolumeSettings() {
        try {
            
            audio.ringerMode = AudioManager.RINGER_MODE_NORMAL

            
            val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audio.setStreamVolume(
                AudioManager.STREAM_ALARM,
                maxVolume,
                0 
            )

            
            volumeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
                
                if (focusChange != AudioManager.AUDIOFOCUS_GAIN) {
                    audio.setStreamVolume(
                        AudioManager.STREAM_ALARM,
                        maxVolume,
                        0
                    )
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setOnAudioFocusChangeListener(volumeListener)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(false)
                    .build()
                audio.requestAudioFocus(focusRequest)
            } else {
                @Suppress("DEPRECATION")
                audio.requestAudioFocus(
                    volumeListener,
                    AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startMaxVolumeAlarm(uri: Uri) {
        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(this@AlarmService, uri)

                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )

                
                setVolume(1.0f, 1.0f)

                isLooping = true

                setOnPreparedListener { mp ->
                    mp.start()
                }

                setOnErrorListener { _, what, extra ->
                    
                    try {
                        reset()
                        setDataSource(
                            this@AlarmService,
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        )
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        isLooping = true
                        prepareAsync()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    false
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startVibration() {
        try {
            val pattern = longArrayOf(0, 1000, 500, 1000, 500)
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(
                        VibrationEffect.createWaveform(pattern, 0),
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(pattern, 0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAlarm() {
        isRunning = false

        
        try {
            player?.stop()
            player?.release()
            player = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        
        try {
            audio.abandonAudioFocus(volumeListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        
        
        
        

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }
}