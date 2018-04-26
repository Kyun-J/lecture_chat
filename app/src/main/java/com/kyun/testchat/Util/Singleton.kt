package com.kyun.testchat.Util

import com.kyun.testchat.Realm.Migration
import io.realm.RealmConfiguration
import java.util.*

object Singleton {

    val mConfig = RealmConfiguration.Builder().schemaVersion(0).migration(Migration()).build()

    fun longToDateString(time : Long) : String {
        val calT = Calendar.getInstance()
        val calB = Calendar.getInstance()
        calB.timeInMillis = time
        if(calT.timeInMillis - calB.timeInMillis >= 2*24*60*60*1000) { //2일 이상전
            val month = (calB.get(Calendar.MONTH)+1).toString()
            val date = calB.get(Calendar.DATE).toString()
            if(calT.get(Calendar.YEAR) != calB.get(Calendar.YEAR)) return calB.get(Calendar.YEAR).toString()+". "+month+". "+date
            else return month+"월 "+date+"일"
        } else if(calT.get(Calendar.DAY_OF_WEEK) != calB.get(Calendar.DAY_OF_WEEK)) { //하루 전
            return "어제"
        } else {
            val hour = if(calB.get(Calendar.HOUR) == 0) 12 else calB.get(Calendar.HOUR)
            val min = if(calB.get(Calendar.MINUTE) < 10) "0"+calB.get(Calendar.MINUTE) else calB.get(Calendar.MINUTE)
            val tt = if(calB.get(Calendar.HOUR_OF_DAY) < 12) "오전" else "오후"
            return tt + " " + hour + ":" + min
        }
    }

    fun longToTimeString(time : Long) : String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        val hour = if(cal.get(Calendar.HOUR) == 0) 12 else cal.get(Calendar.HOUR)
        val min = if(cal.get(Calendar.MINUTE) < 10) "0"+cal.get(Calendar.MINUTE) else cal.get(Calendar.MINUTE)
        val tt = if(cal.get(Calendar.HOUR_OF_DAY) < 12) "오전" else "오후"
        return tt + " " + hour + ":" + min
    }
}