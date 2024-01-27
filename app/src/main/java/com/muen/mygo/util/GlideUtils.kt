package com.muen.mygo.util

import android.app.Application
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation

object GlideUtils {
    private lateinit var mContext: Application
    fun init(application: Application) {
        mContext = application
    }

    /**
     * 加载网络图片
     */
    fun loadUrl(url: String, imageView: ImageView,isGlass: Boolean = false) {
        if(isGlass){
            Glide
                .with(mContext)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())      //淡入
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10)))  //毛玻璃效果
                .into(imageView)
        }else{
            Glide
                .with(mContext)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())      //淡入
                .into(imageView)
        }

    }

    /**
     * 加载网络图片圆形
     */
    fun loadUrlCircle(url: String, imageView: ImageView) {
        Glide
            .with(mContext)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())      //淡入
            .circleCrop()
            .into(imageView)
    }

    /**
     * 加载本地动图
     */
    fun loadLocalGif(resourceId: Int, imageView: ImageView){
        Glide
            .with(mContext)
            .asGif()
            .load(resourceId)
            .transition(DrawableTransitionOptions.withCrossFade())      //淡入
            .into(imageView)
    }
}