package com.kyun.testchat.Main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.kyun.testchat.Chat.ChatAdapter
import com.kyun.testchat.Chat.Chatitem
import com.kyun.testchat.R
import com.kyun.testchat.Realm.Chat
import com.kyun.testchat.Util.Singleton
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var adapter : ChatAdapter? = null //리사이클러뷰 어댑터
    private var layoutManager : LinearLayoutManager? = null //리사이클러뷰 레이아웃 매니저

    private var maxB : Int = 0 //스크린 맨 아래의 y값
    private var keyB : Int = 0 //키보드 상단의 y값
    private var rState : Boolean = true //키보드 상태(올라옴, 내려옴)
    private var isAble : Boolean = false //키보드 위치만큼 리사이클러뷰가 이동할 수 있는지

    private val socket = IO.socket("http://49.236.136.85:8010") //소켓 서버
    private val realm = Realm.getDefaultInstance() //

    private var LastT : Long = 0 //마지막으로 로딩한 채팅 시간
    private var LoadLastChat : Boolean = false //가장 마지막 채팅 로딩 여부

    private var userName : String = "" //사용자 이름

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userName = intent.getStringExtra(resources.getString(R.string.user_name)) //intent로 사용자 이름 받아옴

        //소켓 설정
        socket.on(Socket.EVENT_CONNECT, { //소켓 연결되었을때
            socket.emit("join","room")  //"room"이라는 채팅방에 입장
        })

        socket.on("chat", { //채팅이 올때(키값이 chat)
            chat ->
            val data = JsonParser().parse(chat[0].toString()).asJsonArray //데이터 json형식으로 받아옴
            runOnUiThread {
                //메인스레드(uithread)에서 ui작업
                realm.executeTransaction { realm ->
                    realm.insert(Chat().set(data[0].asString, data[1].asString, data[2].asLong)) //realm에 저장
                }
                adapter?.addData(0, Chatitem().set(Chatitem.youChatStart, data[1].asString, data[0].asString, Singleton.longToTimeString(data[2].asLong)))//새로온 채팅 표시
                main_recyler.smoothScrollToPosition(0) //맨 아래로 이동
            }
        })
        //소켓 설정 끝
        socket.connect() //소켓 연결

        adapter = ChatAdapter(chatLoad(realm.where(Chat::class.java).sort("time",Sort.DESCENDING).findAll())) //어댑터 생성, 초기 채팅내용 불러옴
        main_recyler.adapter = adapter //어댑터 할당
        layoutManager = LinearLayoutManager(this) //레이아웃메니저 생성
        layoutManager?.reverseLayout = true //아래서부터 아이템 배열
        layoutManager?.stackFromEnd = true //리사이클러뷰에 데이터를 아래 아이템서부터 넣음
        main_recyler.layoutManager = layoutManager //레이아웃메니저 할당

        main_recyler.overScrollMode = RecyclerView.OVER_SCROLL_NEVER //스크롤 늘어남 애니메이션 없엠

        main_recyler.addOnScrollListener(object : RecyclerView.OnScrollListener() { //리사이클러뷰 안의 스크롤 리스너 생성
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastp = layoutManager?.findLastVisibleItemPosition() //현재 보이는 마지막 포지션
                if(lastp?.plus(1) == adapter?.data?.size && !LoadLastChat) //채팅내용 추가로딩 여부
                    adapter?.addData(chatLoad(realm.where(Chat::class.java).lessThan("time",LastT).sort("time", Sort.DESCENDING).findAll()))//추가로딩
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isAble = layoutManager?.computeScrollVectorForPosition(0)?.y == 1.0.toFloat() //스크롤시마다 키보드가 올라올 때 정상적으로 스크롤 가능한지 여부 판단
            }
        })

        main_input.addOnLayoutChangeListener { view, l, t, r, b, ol, ot, or, ob -> //채팅 입력받는 뷰의 레이아웃 변경여부 리스너 생성
            if(maxB == 0) maxB = b //maxB값 초기화
            if(b != maxB) keyB = maxB - b //키보드 상단 y값 실시간으로 설정. 키보드 크기가 바뀔수 있기 때문
            if((b != maxB && rState) || (b == maxB && !rState)) { //키보드가 올라오거나 내려올 때
                if(rState) { //키보드가 내려간 상태일때 키보드를 올리면
                    main_recyler.postDelayed({ main_recyler.smoothScrollBy(0,keyB)},100) //키보드 크기만큼 위로 스크롤
                } else { //키보드가 올라간 상태일때 키보드를 내리면
                    if(isAble) //정상적으로 키보드 크기만큼 아래로 스크롤 가능하면
                        main_recyler.postDelayed({ main_recyler.smoothScrollBy(0,-keyB)},100) //키보드 크기만큼 아래로 스크롤
                    else
                        main_recyler.postDelayed({ main_recyler.smoothScrollToPosition(0)},100) //맨 아래로 스크롤
                }
                rState = !rState //키보드 올라옴 -> 내려옴으로, 내려옴 -> 올라옴으로 변경
            }
        }

        main_btn.setOnClickListener { //텍스트 전송
            val text = main_edit.text.toString() //텍스트 읽음
            val time = Calendar.getInstance().timeInMillis //현재 시간 설정
            val data = JsonArray()
            data.add(text)
            data.add(userName)
            data.add(time.toString())//json에 데이터 삽입
            socket.emit("chat",data.asJsonArray.toString())//데이터 전송
            realm.executeTransaction {
                realm.insert(Chat().set(text,userName,time,true))//realm에 저장
            }
            adapter?.addData(0, Chatitem().set(Chatitem.meChatStart,"",text,Singleton.longToTimeString(time))) //리사이클러뷰에 추가
            main_recyler.smoothScrollToPosition(0) //맨 아래로 이동
            main_edit.setText("") //입력창 초기화
        }

        main_recyler.postDelayed({main_recyler.scrollToPosition(0)},100) //초기에 가장 최근 채팅으로 화면 전환
    }


    private fun chatLoad(chat : RealmResults<Chat>) : ArrayList<Chatitem> {
        val result : ArrayList<Chatitem> = ArrayList() //리턴할 데이터들
        val count = if(chat.size > 20) 19 else chat.size - 1 //최대 20개 만큼
        if(count < 19) LoadLastChat = true //로딩한 채팅이 20개 이하면 마지막 로딩
        for(i in 0..count) { //리스트에 채팅내용 삽입
            val c = chat[i]
            if(c != null) {
                if (c.isMe)
                    result.add(Chatitem().set(Chatitem.meChatStart,c.name,c.contents,Singleton.longToTimeString(c.time)))
                else
                    result.add(Chatitem().set(Chatitem.youChatStart,c.name,c.contents,Singleton.longToTimeString(c.time)))
                LastT = c.time //마지막으로 로딩한 시간 재설정
            }
        }
        return result
    }
}
