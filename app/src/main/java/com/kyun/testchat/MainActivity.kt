package com.kyun.testchat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var adapter : ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data : ArrayList<Chatitem> = ArrayList()
        val c = Calendar.getInstance()
        data.add(0,Chatitem().set(Chatitem.timeline,"",(c.get(Calendar.MONTH) + 1).toString() + "월 " + c.get(Calendar.DATE) + "일 ",""))
        data.add(0,Chatitem().set(Chatitem.inout,"","상대방 님이 들어왔습니다.",""))
        data.add(0,Chatitem().set(Chatitem.youChatStart,"상대방","안녕하세요",Singleton.longToTimeString(c.timeInMillis)))
        data.add(0,Chatitem().set(Chatitem.youChatContinue,"","반가워요",""))
        data.add(0,Chatitem().set(Chatitem.meChatStart,"","그래 반가워",Singleton.longToTimeString(c.timeInMillis+60*1000)))
        data.add(0,Chatitem().set(Chatitem.meChatContinue,"","이거 다 그짓말인거 아시져?",""))
        adapter = ChatAdapter(data)

        main_recyler.adapter = adapter
        val layoutmanager = LinearLayoutManager(this)
        layoutmanager.reverseLayout = true
        layoutmanager.stackFromEnd = true
        main_recyler.layoutManager = layoutmanager
        main_btn.setOnClickListener {
            adapter?.addData(0,Chatitem().set(Chatitem.meChatContinue,"",main_edit.text.toString(),""))
            main_edit.setText("")
        }

    }
}
