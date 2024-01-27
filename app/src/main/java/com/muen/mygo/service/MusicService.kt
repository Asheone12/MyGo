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
import com.muen.mygo.source.local.entity.SongEntity
import com.muen.mygo.ui.MainActivity


class MusicService : Service() {
    private lateinit var notification: NotificationCompat.Builder

    override fun onBind(intent: Intent?): IBinder {
        return MusicControl()
    }

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "musicService",
                "音乐播放",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        notification = NotificationCompat.Builder(this, "musicService")
            .setContentTitle("It's MyGo!!!!")
            .setSmallIcon(R.drawable.icon_logo)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon_logo))
            .setContentIntent(pi)

        startForeground(1, notification.build())
    }



    //该内部类包含所有歌曲的操作
    inner class MusicControl : Binder() {
        private lateinit var mediaPlayer: MediaPlayer
        private var playMode = 0
        private var songs: List<SongEntity> = arrayListOf()
        private var currentIndex = 0

        lateinit var indexChangeListener:(Int)->Unit

        fun setOnIndexChangeListener(listener:(Int)->Unit){
            indexChangeListener=listener
            //indexChangeListener(currentIndex)
        }

        fun initMediaPlayer() {
            mediaPlayer = MediaPlayer()
            //设置播放器的播放类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            //播放完毕监听
            mediaPlayer.setOnCompletionListener {
                nextPlay()
                if(playMode != 2){
                    playMusic()
                }
            }
            //播放出现错误监听,直接拦截，若不拦截则会再次调用播放完毕的监听事件，导致连续切歌
            mediaPlayer.setOnErrorListener { _, _, _ ->
                return@setOnErrorListener true
            }

        }

        fun loadSongs(songs: List<SongEntity>) {
            this.songs = songs
        }

        fun setPlayMode(playMode: Int) {
            this.playMode = playMode
        }

        fun setCurrentIndex(currentIndex: Int) {
            this.currentIndex = currentIndex
            indexChangeListener(currentIndex)
        }

        fun currentIndex(): Int {
            return currentIndex
        }

        fun duration(): Int {
            return mediaPlayer.duration
        }

        fun currentPos(): Int {
            return mediaPlayer.currentPosition
        }

        fun prepare(song: SongEntity) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(song.url)
            mediaPlayer.prepare()
            notification.setContentText("正在演奏 ${song.songs} . . .")
            startForeground(1, notification.build())
        }

        fun playMusic() {
            mediaPlayer.start()
        }

        fun pauseMusic() {
            mediaPlayer.pause()
        }

        fun playBackward(){
            if (currentIndex == 0) {
                currentIndex = songs.size - 1
            } else {
                currentIndex--
            }
            indexChangeListener(currentIndex)
        }

        fun playForward(){
            if (currentIndex == songs.size - 1) {
                currentIndex = 0
            } else {
                currentIndex++
            }
            indexChangeListener(currentIndex)
        }

        private fun playRandom(){
            if (songs.size > 1) {
                var random = (songs.indices).random()
                while (currentIndex == random) {
                    random = (songs.indices).random()
                }
                currentIndex = random
            }
            indexChangeListener(currentIndex)
        }

        fun stop() {
            mediaPlayer.stop()
        }

        fun release() {
            mediaPlayer.release()
        }

        fun seekTo(mSec: Int) {
            mediaPlayer.seekTo(mSec)
        }

        fun setLooping(isLooping: Boolean) {
            mediaPlayer.isLooping = isLooping
        }

        private fun nextPlay() {
            pauseMusic()
            when (playMode) {
                0 -> {
                    playForward()
                    prepare(songs[currentIndex])
                }

                1 -> {
                    playRandom()
                    prepare(songs[currentIndex])
                }
                else -> {}
            }
        }
    }


    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

}