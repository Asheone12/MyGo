package com.muen.mygo.ui.video

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.muen.mygo.ARouteAddress
import com.muen.mygo.R
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
    }

    private fun initPlayer(){
        exoPlayer = ExoPlayer.Builder(this).build().also {
            val rawSource =  RawResourceDataSource(this)
            rawSource.open(DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.soyo)))
            viewBinding.playView.player = it
            it.setMediaItem(MediaItem.fromUri(rawSource.uri!!))
        }

    }

}