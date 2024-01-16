package com.muen.mygo.http

import com.muen.mygo.MMKVManage.SUCCESS_CODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun <T> httpFlow(block: suspend () -> HttpResult<T>) = flow {
    emit(block())
}.map {
    if (it.code == SUCCESS_CODE) {
        DataResult(it.data)
    } else {
        CodeResult(it.code, it.msg)
    }
}.catch { cause ->
    cause.printStackTrace()
    emit(ErrorResult(cause))
}.flowOn(Dispatchers.IO)//.onstart{}

suspend inline fun <T> Flow<Result<T>>.httpCollect(handler: HttpResultHandler<T>) {
    this.collect {
        when (it) {
            is DataResult -> {
                handler.onDataResult(it.data)
            }

            is CodeResult -> {
                handler.onCodeResult(it.code, it.msg)
            }

            is ErrorResult -> {
                handler.onErrorResult(it.throwable)
            }
        }
    }
}