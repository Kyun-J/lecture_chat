package com.kyun.testchat

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import com.google.gson.JsonElement
import com.kyun.testchat.Util.Singleton
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class myService : Service() {

    private val mBinder : IBinder = myBinder()
    private val CallbackList : ArrayList<myCallBack> = ArrayList()


    inner class myBinder : Binder() {
        fun getService() : myService { return this@myService }
    }

    interface myCallBack {
        fun recData(data : String)
    }

    fun registerCallback(callback : myCallBack) {
        CallbackList.add(callback)
    }

    fun unregisterCallback(callback : myCallBack) {
        CallbackList.remove(callback)
    }

    fun getdata(data : String, callback : (JsonElement) -> Unit) {
        Singleton.RetroS.test(data).enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if(response.isSuccessful) {
                    callback(response.body()!!)
                } else {
                    response.errorBody()
                }
            }

            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {

            }
        })
    }

    fun postdata(data : String){
        Singleton.RetroS.test2("data").enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    for(c in CallbackList) c.recData(response.body().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //재시작 알람
            (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setAndAllowWhileIdle(AlarmManager.RTC, Calendar.getInstance().timeInMillis + 5000,
                    PendingIntent.getService(this, 1, Intent(this, myService::class.java), 0))
        } else {
            (getSystemService(Context.ALARM_SERVICE) as AlarmManager).set(AlarmManager.RTC, Calendar.getInstance().timeInMillis + 5000,
                    PendingIntent.getService(this, 1, Intent(this, myService::class.java), 0))
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

}