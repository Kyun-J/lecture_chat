package com.kyun.testchat

import com.chad.library.adapter.base.entity.MultiItemEntity

class Chatitem : MultiItemEntity {

    companion object {
        val inout = 0
        val timeline = 1
        val meChatStart = 2
        val meChatContinue = 3
        val youChatStart = 4
        val youChatContinue = 5
    }

    var itemT = -1 //아이템타입
    var name : String = "" //이름
    var contents : String = "" //표시내용
    var time : String = "" //시간

    fun set(itemtype : Int, name : String, contents : String, time : String) : Chatitem {
        this.itemT = itemtype
        this.name = name
        this.contents = contents
        this.time = time

        return this
    }

    override fun getItemType(): Int {
        return itemT
    }
}