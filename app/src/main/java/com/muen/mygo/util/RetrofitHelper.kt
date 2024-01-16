package com.muen.mygo.util

import android.os.Build
import com.muen.mygo.MMKVManage.HTTP_TIME_OUT
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Header配置
 */
object HeaderInterceptorKt {
    val token = ""
    val headerInterceptor: Interceptor = Interceptor { chain ->
        val original = chain.request()
        val originalHttpUrl = original.url
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("systemVer", Build.VERSION.RELEASE)
            .addQueryParameter("phone_brand", Build.MANUFACTURER)
            .addQueryParameter("device", "Android")
            .addQueryParameter("phone_model", Build.MODEL)

        val request = original.newBuilder()
            .addHeader("Authorization", token)
            .url(url.build())
        chain.proceed(request.build())
    }

}

/**
 * 快速创建一个OkHttpClient.Builder
 */
fun defaultHttpClientBuilder(
    vararg interceptor: Interceptor
): OkHttpClient.Builder {
    val builder = OkHttpClient.Builder()
        .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)

    if (interceptor.isNotEmpty()) {
        interceptor.forEach {
            builder.addInterceptor(it)
        }
    }
    return builder
}

/**
 * 快速创建一个Retrofit.Builder
 */
fun defaultRetrofitBuilder(url: String, client: OkHttpClient): Retrofit.Builder {
    return Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
}

/**
 * 快速创建一个OkHttpClient
 */
fun defaultHttpClient(vararg interceptor: Interceptor): OkHttpClient {
    return defaultHttpClientBuilder(*interceptor).build()
}

/**
 * 快速创建一个Retrofit
 */
fun defaultRetrofit(url: String, client: OkHttpClient): Retrofit {
    return defaultRetrofitBuilder(url, client).build()
}

