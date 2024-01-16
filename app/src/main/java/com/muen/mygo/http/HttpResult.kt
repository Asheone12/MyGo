package com.muen.mygo.http

data class HttpResult<T>(val code: Int, val msg: String?, val data: T?)

sealed class Result<T>

data class DataResult<T>(val data: T?) : Result<T>()

data class CodeResult<T>(val code: Int, val msg: String?) : Result<T>()

data class ErrorResult<T>(val throwable: Throwable) : Result<T>()