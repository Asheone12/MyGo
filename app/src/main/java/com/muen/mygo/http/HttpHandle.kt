package com.muen.mygo.http

import com.muen.mygo.util.ToastUtils
import timber.log.Timber

interface HttpResultHandler<T> {
    suspend fun onDataResult(data: T?)
    suspend fun onCodeResult(code: Int, msg: String?)
    suspend fun onErrorResult(throwable: Throwable)
}

abstract class CommonHandler<T> : HttpResultHandler<T> {
    override suspend fun onDataResult(data: T?) {
        Timber.tag("handle").d("data = $data")
    }

    override suspend fun onCodeResult(code: Int, msg: String?) {
        Timber.tag("handle").d("code = $code, message = $msg")
        ToastUtils.toast(msg!!)
    }

    override suspend fun onErrorResult(throwable: Throwable) {
        Timber.tag("handle").d("throwable = $throwable")
    }
}