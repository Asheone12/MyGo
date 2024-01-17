package com.muen.mygo.util

object TimeUtils {
    fun calculateTime(time: Int): String {
        val minute = time / 60
        val second = time % 60
        var minuteStr = minute.toString()
        var secondStr =second.toString()
        if(minute<10){
            minuteStr = "0$minute"
        }
        if(second<10){
            secondStr = "0$second"
        }
        return "$minuteStr:$secondStr"
    }

}