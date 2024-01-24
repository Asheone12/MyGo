package com.muen.mygo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.muen.mygo.R
import com.muen.mygo.ui.MainActivity


class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onBind(intent: Intent?): IBinder {
        return MusicControl()
    }

    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("musicService","音乐播放",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this,"musicService")
            .setContentTitle("It's MyGo!!!!")
            .setContentText("正在演奏春日影 . . .")
            .setSmallIcon(R.drawable.icon_logo)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.icon_logo))
            .setContentIntent(pi)
            .build()
        startForeground(1,notification)
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        //设置播放器的播放类型
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    //该内部类包含所有歌曲的操作
    inner class MusicControl:Binder() {
        fun prepare(url:String){
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
        }

        fun duration():Int{
            return mediaPlayer.duration
        }

        fun currentPos():Int{
            return mediaPlayer.currentPosition
        }

        fun playMusic() {
            mediaPlayer.start()
        }
        fun pauseMusic() {
            mediaPlayer.pause()
        }

        fun stop(){
            mediaPlayer.stop()
        }

        fun release(){
            mediaPlayer.release()
        }

        fun seekTo(mSec:Int){
            mediaPlayer.seekTo(mSec)
        }

        fun setLooping(isLoop:Boolean){
            mediaPlayer.isLooping = isLoop
        }

        fun completionListener(handler:(MediaPlayer) -> Unit){
            mediaPlayer.setOnCompletionListener {
                handler.invoke(it)
            }
        }
    }



    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

}