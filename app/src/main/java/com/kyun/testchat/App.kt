package com.kyun.testchat

import android.app.Application
import com.kyun.testchat.Util.Singleton
import io.realm.Realm

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(Singleton.mConfig)

    }

}