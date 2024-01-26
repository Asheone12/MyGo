package com.muen.mygo.di

import android.content.Context
import com.muen.mygo.BuildConfig.BASE_URL
import com.muen.mygo.source.local.dao.SongDao
import com.muen.mygo.source.local.db.SongDB
import com.muen.mygo.source.network.AppServiceApi
import com.muen.mygo.util.HeaderInterceptorKt
import com.muen.mygo.util.defaultHttpClient
import com.muen.mygo.util.defaultRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): SongDB {
        return SongDB.getDatabase(context)
    }

    @Provides
    fun provideWordDao(database: SongDB): SongDao = database.songDao()
}