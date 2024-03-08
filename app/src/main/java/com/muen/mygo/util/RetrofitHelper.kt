package com.muen.mygo.util

import com.muen.mygo.BuildConfig.BASE_URL
import com.muen.mygo.BuildConfig.BASE_URL_PAUGRAM
import com.muen.mygo.MMKVManage.HTTP_TIME_OUT
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Header配置
 */
object HeaderInterceptorKt {
    val headerInterceptor: Interceptor = Interceptor { chain ->
        val request = chain.request()
        val httpUrl = request.url
        val builder = request.newBuilder()
        val headerValues = request.headers("url_name")
        if(headerValues.isNotEmpty()){
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder.removeHeader("url_name")
            val headerValue = headerValues[0]
            val newBaseUrl:HttpUrl
            if(headerValue == "tenApi"){
                newBaseUrl = BASE_URL.toHttpUrlOrNull()!!
            }else if(headerValue == "paugram"){
                newBaseUrl = BASE_URL_PAUGRAM.toHttpUrlOrNull()!!
            }else{
                newBaseUrl = httpUrl
            }
            val newHttpUrl = httpUrl
                .newBuilder()
                .scheme(newBaseUrl.scheme)
                .host(newBaseUrl.host)
                .port(newBaseUrl.port)
                .build()
            chain.proceed(builder.url(newHttpUrl).build())
        }else{
            chain.proceed(request)
        }
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

