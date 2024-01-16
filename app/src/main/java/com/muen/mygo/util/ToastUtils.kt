package com.muen.mygo.util

import android.app.Application
import android.widget.Toast
import androidx.annotation.MainThread

object ToastUtils {

    private var application: Application? = null

    fun init(application: Application) {
        ToastUtils.application = application
    }

    @MainThread
    fun toast(id: Int) {
        Toast.makeText(application,id,Toast.LENGTH_SHORT).show()
    }

    @MainThread
    fun toast(text: String) {
        Toast.makeText(application,text,Toast.LENGTH_SHORT).show()
    }

    @MainThread
    fun toast(id: Int, vararg formatArgs: Any) {
        val text = application?.getString(id, *formatArgs)
        if (!text.isNullOrEmpty()) {
            toast(text)
        }
    }

    @MainThread
    fun longToast(id: Int) {
        Toast.makeText(application,id,Toast.LENGTH_LONG).show()
    }

    @MainThread
    fun longToast(text: String) {
        Toast.makeText(application,text,Toast.LENGTH_LONG).show()
    }
}