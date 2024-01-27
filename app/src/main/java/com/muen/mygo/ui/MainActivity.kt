package com.muen.mygo.ui

import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.muen.mygo.ARouteAddress
import com.muen.mygo.R
import com.muen.mygo.databinding.ActivityMainBinding
import com.muen.mygo.service.MusicService
import com.muen.mygo.source.local.entity.SongEntity
import com.muen.mygo.ui.vm.MainVM
import com.muen.mygo.util.BaseActivity
import com.muen.mygo.util.TimeUtils
import com.muen.mygo.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.Timer
import java.util.TimerTask

@Route(path = ARouteAddress.APP_MAIN)
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by viewModels<MainVM>()
    private lateinit var animator: ObjectAnimator
    private val timer = Timer()
    private var isTick = false   //是否正在计时
    private var playStatus = 0   //0播放 1暂停
    private var isSeeking = false   //是否正在拖动进度条

    private var playMode = 0      //0列表循环 1随机播放 2单曲循环
    private var songs: List<SongEntity> = arrayListOf()

    private val musicConnection = MusicConnection()
    lateinit var musicControl: MusicService.MusicControl

    inner class MusicConnection : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            musicControl = p1 as MusicService.MusicControl
            musicControl.initMediaPlayer()
            viewModel.loadSongs {
                songs = it
                musicControl.loadSongs(songs)
                loadComplete(songs[0])
            }
            musicControl.setOnIndexChangeListener {
                songPause()
                loadComplete(songs[it])
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }

    override fun onCreateViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, musicConnection, BIND_AUTO_CREATE)

        //设置动画匀速运动
        animator = ObjectAnimator.ofFloat(viewBinding.imgAcg, "rotation", 0f, 360f)
        animator.duration = 6500
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = -1       //设置重复次数为无数次
        animator.repeatMode = ObjectAnimator.RESTART
        //设置播放器的播放类型

        Glide.with(this).asGif().load(R.drawable.cry).into(viewBinding.gifView)
    }

    override fun initListener() {
        super.initListener()
        viewBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewBinding.startTime.text =
                    TimeUtils.calculateTime(musicControl.currentPos() / 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
                musicControl.seekTo(seekBar?.progress!!)
                viewBinding.startTime.text =
                    TimeUtils.calculateTime(musicControl.currentPos() / 1000)
            }

        })

        viewBinding.play.setOnClickListener {
            when (playStatus) {
                0 -> {
                    songPause()
                    musicControl.pauseMusic()
                }

                else -> {
                    songPlay()
                    musicControl.playMusic()
                }
            }
        }

        viewBinding.backward.setOnClickListener {
            songPause()
            musicControl.playBackward()
        }

        viewBinding.forward.setOnClickListener {
            songPause()
            musicControl.playForward()
        }

        viewBinding.playMode.setOnClickListener {
            when (playMode) {
                0 -> {
                    playMode = 1
                    musicControl.setPlayMode(1)
                    viewBinding.playMode.setImageResource(R.drawable.play_random)
                    musicControl.setLooping(false)
                }

                1 -> {
                    playMode = 2
                    musicControl.setPlayMode(2)
                    viewBinding.playMode.setImageResource(R.drawable.play_single)
                    musicControl.setLooping(true)
                }

                else -> {
                    playMode = 0
                    musicControl.setPlayMode(0)
                    viewBinding.playMode.setImageResource(R.drawable.play_list_loop)
                    musicControl.setLooping(false)
                }
            }
        }

        viewBinding.floatBtn.setOnClickListener{
            //彩蛋
            if( songs[musicControl.currentIndex()].sid == "2097486090"){
                if(viewBinding.gifView.visibility == View.GONE){
                    viewBinding.gifView.visibility = View.VISIBLE
                }else{
                    viewBinding.gifView.visibility = View.GONE
                }
            }else{
                ToastUtils.toast("工号2568，客服小祥为您服务")
            }
        }

        viewBinding.gifView.setOnClickListener {
            ARouter.getInstance().build(ARouteAddress.APP_VIDEO).navigation()
        }
    }


    override fun observerData() {
        super.observerData()
        viewModel.songResult.observe(this) {
            val songEntity = SongEntity(
                it?.id!!,
                it.songs,
                it.sings,
                it.album,
                it.cover,
                it.url
            )
            viewModel.selectSong(it.id) { song ->
                if (song == null) {
                    ToastUtils.toast("数据库内未获取该歌曲，正在添加 . . .")
                    viewModel.insertWord(songEntity)
                }
            }

            loadComplete(songEntity)
        }
    }

    private fun loadComplete(song: SongEntity) {
        viewBinding.seekBar.progress = 0
        viewBinding.title.text = song.songs
        viewBinding.singer.text = song.sings
        Glide.with(this).load(song.cover).transition(DrawableTransitionOptions.withCrossFade())
            .circleCrop().into(viewBinding.imgAcg)

        Glide.with(this).load(song.cover)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10)))
            .into(viewBinding.ivBg)

        musicControl.prepare(song)
        songPlay()


        viewBinding.loadView.visibility = View.GONE
        viewBinding.content.visibility = View.VISIBLE

        viewBinding.seekBar.max = musicControl.duration()
        viewBinding.endTime.text = TimeUtils.calculateTime(musicControl.duration() / 1000)
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (isTick && !isSeeking) {
                    viewBinding.seekBar.progress = musicControl.currentPos()
                }
            }

        }, 0, 1000)

        viewBinding.floatBtn.visibility = View.VISIBLE
        //彩蛋
        if (songs[musicControl.currentIndex()].sid == "2097486090") {
            //viewBinding.floatBtn.visibility = View.VISIBLE
        } else {
            viewBinding.gifView.visibility = View.GONE
        }

    }


    private fun songPlay() {
        if (musicControl.currentPos() >= musicControl.duration()) {
            musicControl.seekTo(0)
        }

        viewBinding.play.setImageResource(R.drawable.play)
        if (animator.isStarted) {
            animator.resume()
        } else {
            animator.start()
        }
        musicControl.playMusic()

        isTick = true
        playStatus = 0
    }

    private fun songPause() {
        viewBinding.play.setImageResource(R.drawable.pause)
        animator.pause()
        musicControl.pauseMusic()
        isTick = false
        playStatus = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        musicControl.stop()
        musicControl.release()
        viewBinding.imgAcg.clearAnimation()
    }
}