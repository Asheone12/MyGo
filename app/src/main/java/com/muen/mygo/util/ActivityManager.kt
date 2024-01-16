package com.muen.mygo.util

import android.app.Activity


object ActivityManager {
    private val hashSet = LinkedHashSet<Activity>()

    fun isOnScreen(): Boolean {
        return hashSet.size > 0
    }

    fun addActivity(activity: Activity?) {
        try {
            hashSet.add(activity!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeActivity(activity: Activity?) {
        try {
            hashSet.remove(activity)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 调用此方法用于销毁所有的Activity，然后我们在调用此方法之前，调到登录的Activity
     */
    fun exit() {
        try {
            for (activity in hashSet) {
                if(!activity.isFinishing){
                    activity.finish()
                }
            }
            hashSet.clear()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getTopActivity(): Activity? {
        val iterator: Iterator<Activity> = hashSet.iterator()
        var topActivity: Activity? = null
        while (iterator.hasNext()) {
            topActivity = iterator.next()
        }
        return topActivity
    }

    fun finishSingleActivityByClass(cls: Class<*>) {
        var tempActivity: Activity? = null
        for (temp in hashSet) {
            if (temp.javaClass == cls) {
                tempActivity = temp
            }
        }
        removeActivity(tempActivity)
    }

    fun findActivity(cls: Class<*>): Activity? {
        var tempActivity: Activity? = null
        for (temp in hashSet) {
            if (temp.javaClass == cls) {
                tempActivity = temp
                break
            }
        }
        return tempActivity
    }

    fun hasActivity(cls: Class<*>): Boolean {
        for (temp in hashSet) {
            if (temp.javaClass == cls) {
                return true
            }
        }
        return false
    }

    fun hasActivityNotFinished(cls: Class<*>): Boolean {
        for (temp in hashSet) {
            if (temp.javaClass == cls && !temp.isFinishing) {
                return true
            }
        }
        return false
    }

}