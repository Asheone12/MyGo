package com.muen.mygo.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
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
import com.muen.mygo.service.MusicPlayService
import com.muen.mygo.ui.vm.MainVM
import com.muen.mygo.util.BaseActivity
import com.muen.mygo.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.Timer
import java.util.TimerTask

@Route(path = ARouteAddress.APP_MAIN)
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by viewModels<MainVM>()
    private val mediaPlayer = MediaPlayer()
    private lateinit var animator: ObjectAnimator
    private val timer = Timer()
    private var isTick = false   //是否正在计时
    private var playStatus = 0   //0播放 1暂停
    private var isSeeking = false   //是否正在拖动进度条
    private var currentIndex = 0    //当前播放歌曲的序号
    private var playMode = 0      //0列表循环 1随机播放 2单曲循环
    private val songList = arrayListOf<Long>(2097486090, 2097485070, 2097486092, 2097486091)
    override fun onCreateViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initData() {
        super.initData()
        viewModel.getSong(songList[currentIndex])
    }

    override fun initView() {
        super.initView()
        startService(Intent(this,MusicPlayService::class.java))
        //设置动画匀速运动
        animator = ObjectAnimator.ofFloat(viewBinding.imgAcg, "rotation", 0f, 360f)
        animator.duration = 6500
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = -1       //设置重复次数为无数次
        animator.repeatMode = ObjectAnimator.RESTART
        //设置播放器的播放类型
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        Glide.with(this).asGif().load(R.drawable.cry).into(viewBinding.gifView)
    }

    override fun initListener() {
        super.initListener()
        viewBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewBinding.startTime.text =
                    TimeUtils.calculateTime(mediaPlayer.currentPosition / 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
                mediaPlayer.seekTo(seekBar?.progress!!)
                viewBinding.startTime.text =
                    TimeUtils.calculateTime(mediaPlayer.currentPosition / 1000)
            }

        })

        mediaPlayer.setOnCompletionListener {
            playNext()
        }

        viewBinding.play.setOnClickListener {
            when (playStatus) {
                0 -> songPause()
                else -> songPlay()
            }
        }

        viewBinding.backward.setOnClickListener {
            if (currentIndex == 0) {
                currentIndex = songList.size - 1
            } else {
                currentIndex--
            }
            songPause()
            viewBinding.seekBar.progress = 0
            viewBinding.loadView.visibility = View.VISIBLE
            viewBinding.content.visibility = View.GONE
            viewModel.getSong(songList[currentIndex])
        }

        viewBinding.forward.setOnClickListener {
            if (currentIndex == songList.size - 1) {
                currentIndex = 0
            } else {
                currentIndex++
            }
            songPause()
            viewBinding.seekBar.progress = 0
            viewBinding.loadView.visibility = View.VISIBLE
            viewBinding.content.visibility = View.GONE
            viewModel.getSong(songList[currentIndex])
        }

        viewBinding.playLoop.setOnClickListener {
            when (playMode) {
                0 -> {
                    playMode = 1
                    viewBinding.playLoop.setImageResource(R.drawable.play_random)
                    mediaPlayer.isLooping = false
                }

                1 -> {
                    playMode = 2
                    viewBinding.playLoop.setImageResource(R.drawable.play_single)
                    mediaPlayer.isLooping = true
                }

                else -> {
                    playMode = 0
                    viewBinding.playLoop.setImageResource(R.drawable.play_list_loop)
                    mediaPlayer.isLooping = false
                }
            }
            Log.d("loop", "playMode = $playMode")
        }

        viewBinding.gifView.setOnClickListener {
            ARouter.getInstance().build(ARouteAddress.APP_VIDEO).navigation()
        }
    }


    override fun observerData() {
        super.observerData()
        viewModel.songResult.observe(this) {
            viewBinding.title.text = it?.songs
            viewBinding.singer.text = it?.sings
            Glide.with(this).load(it?.cover).transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop().into(viewBinding.imgAcg)

            Glide.with(this).load(it?.cover)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10)))
                .into(viewBinding.ivBg)

            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(it?.url)
            mediaPlayer.prepare()

            viewBinding.loadView.visibility = View.GONE
            viewBinding.content.visibility = View.VISIBLE

            viewBinding.seekBar.max = mediaPlayer.duration
            viewBinding.endTime.text = TimeUtils.calculateTime(mediaPlayer.duration / 1000)
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (isTick && !isSeeking) {
                        viewBinding.seekBar.progress = mediaPlayer.currentPosition
                    }
                }

            }, 0, 1000)

            //彩蛋
            if (songList[currentIndex] == 2097486090L) {
                viewBinding.gifView.visibility = View.VISIBLE
            } else {
                viewBinding.gifView.visibility = View.GONE
            }
            songPlay()

        }
    }

    private fun getSongList(){
        for(song in songList){
            viewModel.getSong(song)
        }
    }

    private fun songPlay() {
        if (mediaPlayer.currentPosition >= mediaPlayer.duration) {
            mediaPlayer.seekTo(0)
        }
        viewBinding.play.setImageResource(R.drawable.play)
        if (animator.isStarted) {
            animator.resume()
        } else {
            animator.start()
        }
        mediaPlayer.start()
        isTick = true
        playStatus = 0
    }

    private fun songPause() {
        viewBinding.play.setImageResource(R.drawable.pause)
        animator.pause()
        mediaPlayer.pause()
        isTick = false
        playStatus = 1
    }

    private fun playNext() {
        songPause()
        when (playMode) {
            0 -> {
                Log.d("loop", "即将列表循环")
                if (currentIndex == songList.size - 1) {
                    currentIndex = 0
                } else {
                    currentIndex++
                }
                viewBinding.seekBar.progress = 0
                viewBinding.loadView.visibility = View.VISIBLE
                viewBinding.content.visibility = View.GONE
                viewModel.getSong(songList[currentIndex])
            }

            1 -> {
                Log.d("loop", "即将随机播放")
                if (songList.size > 1) {
                    var random = (0 until songList.size).random()
                    while (currentIndex == random) {
                        random = (0 until songList.size).random()
                    }
                    currentIndex = random
                }
                viewBinding.seekBar.progress = 0
                viewBinding.loadView.visibility = View.VISIBLE
                viewBinding.content.visibility = View.GONE
                viewModel.getSong(songList[currentIndex])

            }

            else -> {}
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayer.stop()
        mediaPlayer.release()
        viewBinding.imgAcg.clearAnimation()
    }
}