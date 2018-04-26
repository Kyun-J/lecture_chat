package com.kyun.testchat.Realm

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
class Chat : RealmModel {

    var contents : String = ""
    var name : String = ""
    var time : Long = 0
    var isMe : Boolean = false

    fun set (contents : String , name : String, time : Long) : Chat {
        this.contents = contents
        this.name = name
        this.time = time

        return this
    }

    fun set (contents : String , name : String, time : Long, isMe : Boolean) : Chat {
        this.contents = contents
        this.name = name
        this.time = time
        this.isMe = isMe

        return this
    }

}