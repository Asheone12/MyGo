package com.muen.mygo.di

import com.muen.mygo.BuildConfig.BASE_URL
import com.muen.mygo.source.network.AppServiceApi
import com.muen.mygo.util.HeaderInterceptorKt
import com.muen.mygo.util.defaultHttpClient
import com.muen.mygo.util.defaultRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDefaultRetrofit(): Retrofit {
        val okhttpClient = defaultHttpClient(HeaderInterceptorKt.headerInterceptor)
        return defaultRetrofit(BASE_URL, okhttpClient)
    }

    @Singleton
    @Provides
    fun provideAppServiceApi(retrofit: Retrofit): AppServiceApi {
        return retrofit.create(AppServiceApi::class.java)
    }
}