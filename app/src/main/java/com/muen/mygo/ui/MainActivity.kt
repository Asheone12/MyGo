package com.muen.mygo.ui

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.muen.mygo.ARouteAddress
import com.muen.mygo.databinding.ActivityMainBinding
import com.muen.mygo.ui.vm.MainVM
import com.muen.mygo.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@Route(path = ARouteAddress.APP_MAIN)
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by viewModels<MainVM>()
    override fun onCreateViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initData() {
        super.initData()
        viewModel.randomACG()
    }

    override fun observerData() {
        super.observerData()
        viewModel.acgResult.observe(this){
            Glide.with(this).load(it?.url).transition(DrawableTransitionOptions.withCrossFade()).into(viewBinding.imgAcg)
        }
    }
}