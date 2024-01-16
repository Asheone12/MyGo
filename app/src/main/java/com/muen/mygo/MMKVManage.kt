package com.muen.mygo

import com.tencent.mmkv.MMKV

object MMKVManage {
    private val mmkv = MMKV.defaultMMKV()

    //常量
    const val SUCCESS_CODE = 200
    const val HTTP_TIME_OUT = 20L

    //缓存变量
    private const val KEY_LAST_WORD = "last_word"

    /**
     * 上一次的一言
     */
    var lastWord: String?
        set(value) {
            mmkv.encode(KEY_LAST_WORD, value)
        }
        get() = mmkv.decodeString(KEY_LAST_WORD) ?: ""

}