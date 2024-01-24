package com.muen.mygo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.muen.mygo.R
import com.muen.mygo.ui.MainActivity


class MusicPlayService : Service() {
    //lateinit var mediaPlayer: MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        //mediaPlayer = MediaPlayer()
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

    //该方法包含关于歌曲的操作
    /*class MyBinder : Binder() {

    }*/


    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

}