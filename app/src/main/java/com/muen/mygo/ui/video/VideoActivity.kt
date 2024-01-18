package com.muen.mygo.ui.video

import android.view.View
import androidx.activity.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.alibaba.android.arouter.facade.annotation.Route
import com.muen.mygo.ARouteAddress
import com.muen.mygo.databinding.ActivityVideoBinding
import com.muen.mygo.ui.video.vm.VideoVM
import com.muen.mygo.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@Route(path = ARouteAddress.APP_VIDEO)
@AndroidEntryPoint
class VideoActivity : BaseActivity<ActivityVideoBinding>() {
    private val viewModel by viewModels<VideoVM>()
    private lateinit var exoPlayer: ExoPlayer
    override fun onCreateViewBinding(): ActivityVideoBinding {
        return ActivityVideoBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        initPlayer()
        hideSystemUi()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    override fun initListener() {
        super.initListener()
        viewBinding.playView.setOnClickListener{
            exoPlayer.prepare()
            exoPlayer.play()
        }
        viewBinding.playView.player?.addListener(object :Player.Listener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)

            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
            }
        })
    }

    private fun initPlayer(){
        exoPlayer = ExoPlayer.Builder(this).build().also {
            viewBinding.playView.player = it
            it.setMediaItem(MediaItem.fromUri("https://cn-zjjh-ct-04-06.bilivideo.com/upgcxcode/35/90/553099035/553099035-1-16.mp4?e=ig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfq9rVEuxTEnE8L5F6VnEsSTx0vkX8fqJeYTj_lta53NCM=&uipk=5&nbs=1&deadline=1705564527&gen=playurlv2&os=bcache&oi=2043500563&trid=0000481124be910e4de8ba44bc7cc4ae5dbfh&mid=0&platform=html5&upsig=ccd3d66e2b1f309876bbe91bb89a4ed3&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&cdnid=6591&bvc=vod&nettype=0&f=h_0_0&bw=50365&logo=80000000"))
        }

    }

    private fun hideSystemUi() {
        viewBinding.playView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


}