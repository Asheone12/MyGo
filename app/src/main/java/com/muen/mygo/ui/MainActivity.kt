package com.muen.mygo.ui

import android.animation.ObjectAnimator
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.muen.mygo.ARouteAddress
import com.muen.mygo.R
import com.muen.mygo.databinding.ActivityMainBinding
import com.muen.mygo.ui.vm.MainVM
import com.muen.mygo.util.BaseActivity
import com.muen.mygo.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@Route(path = ARouteAddress.APP_MAIN)
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by viewModels<MainVM>()
    private val mediaPlayer = MediaPlayer()
    private lateinit var animator:ObjectAnimator
    private val timer = Timer()
    private var isTick = false
    private var isSeeking = false
    override fun onCreateViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initData() {
        super.initData()
        viewModel.getSong(2097486090)
    }

    override fun initView() {
        super.initView()
        //设置动画匀速运动
        animator = ObjectAnimator.ofFloat(viewBinding.imgAcg,"rotation",0f,360f)
        animator.duration = 6500
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = -1       //设置重复次数为无数次
        animator.repeatMode = ObjectAnimator.RESTART
    }

    override fun initListener() {
        super.initListener()
        viewBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer.currentPosition >= mediaPlayer.duration) {
                    isTick = false
                    viewBinding.play.setImageResource(R.drawable.play)
                    mediaPlayer.pause()
                    animator.cancel()
                }
                viewBinding.startTime.text =
                    TimeUtils.calculateTime(mediaPlayer.currentPosition / 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
                mediaPlayer.seekTo(seekBar?.progress!!)
                if (mediaPlayer.currentPosition >= mediaPlayer.duration) {
                    isTick = false
                    viewBinding.play.setImageResource(R.drawable.play)
                    mediaPlayer.pause()
                    animator.cancel()
                }
                viewBinding.startTime.text =
                    TimeUtils.calculateTime(mediaPlayer.currentPosition / 1000)
            }

        })

        viewBinding.play.setOnClickListener {
            isTick = if(isTick){
                viewBinding.play.setImageResource(R.drawable.play)
                animator.pause()
                mediaPlayer.pause()
                false
            }else{
                if(mediaPlayer.currentPosition >= mediaPlayer.duration){
                    mediaPlayer.seekTo(0)
                }
                viewBinding.play.setImageResource(R.drawable.pause)
                if(animator.isStarted){
                    animator.resume()
                }else{
                    animator.start()
                }
                mediaPlayer.start()
                true
            }
        }
    }

    override fun observerData() {
        super.observerData()
        viewModel.songResult.observe(this) {
            Glide.with(this).load(it?.cover).transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop().into(viewBinding.imgAcg)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(it?.url)
            mediaPlayer.prepare()
            viewBinding.loadView.visibility = View.GONE
            viewBinding.progressLayout.visibility = View.VISIBLE
            viewBinding.btnLayout.visibility = View.VISIBLE
            viewBinding.seekBar.max = mediaPlayer.duration
            viewBinding.endTime.text = TimeUtils.calculateTime(mediaPlayer.duration / 1000)
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (isTick && !isSeeking) {
                        viewBinding.seekBar.progress = mediaPlayer.currentPosition
                    }
                }

            }, 0, 50)
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