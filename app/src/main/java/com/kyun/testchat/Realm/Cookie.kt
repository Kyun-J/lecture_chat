package com.kyun.testchat.Realm

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class Cookie : RealmModel {

    var Contents : String = ""

    fun set(contents : String) : Cookie {
        Contents = contents

        return this
    }

}