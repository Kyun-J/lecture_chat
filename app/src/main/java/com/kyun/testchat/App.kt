package com.kyun.testchat

import android.app.Application
import android.content.Intent
import com.kyun.testchat.Util.Singleton
import io.realm.Realm

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(Singleton.mConfig)
        startService(Intent(this,myService::class.java))
    }

}