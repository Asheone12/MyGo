package com.muen.mygo.repo

import com.muen.mygo.http.CommonHandler
import com.muen.mygo.http.httpCollect
import com.muen.mygo.http.httpFlow
import com.muen.mygo.source.network.AppServiceApi
import com.muen.mygo.source.network.entity.ACGImg
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppServiceRepo @Inject constructor(
    private val service: AppServiceApi
) {
    /**
     * 从服务端获取动漫图片
     */
    suspend fun randomACG(handler: CommonHandler<ACGImg>){
        httpFlow {
            service.randomACG("json")
        }.httpCollect(handler)
    }
}