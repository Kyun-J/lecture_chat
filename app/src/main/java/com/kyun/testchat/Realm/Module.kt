package com.kyun.testchat.Realm

import io.realm.annotations.RealmModule

@RealmModule(classes = arrayOf(Chat::class))
class Module