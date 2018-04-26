package com.kyun.testchat.Realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var o = oldVersion

        if(o < newVersion) {
            if (o == 0.toLong()) {
                //변경점 ex ) realm.createObject("aaa").setByte("aaa",0)
                o++
            }
            if (o == 1.toLong()) {
                //변경점....
                o++
            }
            // .....

            if (o == newVersion) {

            }
        }
    }
}