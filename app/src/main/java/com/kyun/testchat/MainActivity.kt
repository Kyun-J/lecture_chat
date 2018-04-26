package com.kyun.testchat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var adapter : ChatAdapter? = null //리사이클러뷰 어댑터
    var layoutManager : LinearLayoutManager? = null //리사이클러뷰 레이아웃 매니저

    private var maxB : Int = 0 //스크린 맨 아래의 y값
    private var keyB : Int = 0 //키보드 상단의 y값
    private var rState : Boolean = true //키보드 상태(올라옴, 내려옴)
    private var isAble : Boolean = false //키보드 위치만큼 리사이클러뷰가 이동할 수 있는지

    val socket = IO.socket("http://49.236.136.85:8010")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //소켓 설정
        socket.on(Socket.EVENT_CONNECT, { //소켓 연결되었을때
            socket.emit("join","room")  //"room"이라는 채팅방에 입장
        })

        socket.on("chat", { //채팅이 올때(키값이 chat)
            runOnUiThread {//메인스레드(uithread)에서 ui작업
                adapter?.addData(0,Chatitem().set(Chatitem.youChatContinue,it[1].toString(),it[0].toString(),it[2].toString()))//새로온 채팅 표시
                main_recyler.smoothScrollToPosition(0)
            }
        })
        //소켓 설정 끝
        socket.connect() //소켓 연결

        val data : ArrayList<Chatitem> = ArrayList() //리사이클러뷰에 표시할 데이터 모음
        val c = Calendar.getInstance() //현재 시각 알아오기

        data.add(0,Chatitem().set(Chatitem.timeline,"",(c.get(Calendar.MONTH) + 1).toString() + "월 " + c.get(Calendar.DATE) + "일 ",""))
        data.add(0,Chatitem().set(Chatitem.inout,"","상대방 님이 들어왔습니다.",""))
        data.add(0,Chatitem().set(Chatitem.youChatStart,"상대방","안녕하세요",Singleton.longToTimeString(c.timeInMillis)))
        data.add(0,Chatitem().set(Chatitem.youChatContinue,"","반가워요",""))
        data.add(0,Chatitem().set(Chatitem.meChatStart,"","그래 반가워",Singleton.longToTimeString(c.timeInMillis+60*1000)))
        data.add(0,Chatitem().set(Chatitem.meChatContinue,"","이거 다 그짓말인거 아시져?",""))

        adapter = ChatAdapter(data) //어댑터 생성
        main_recyler.adapter = adapter //어댑터 할당
        layoutManager = LinearLayoutManager(this) //레이아웃메니저 생성
        layoutManager?.reverseLayout = true //아래서부터 아이템 배열
        layoutManager?.stackFromEnd = true //아이템에 데이터를 아래서부터 넣음

        main_recyler.layoutManager = layoutManager //레이아웃메니저 할당

        main_recyler.addOnScrollListener(object : RecyclerView.OnScrollListener() { //리사이클러뷰 안의 스크롤 리스너 생성
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
            socket.emit("chat",text,"내이름","시간",{ //전송

            })
            adapter?.addData(0,Chatitem().set(Chatitem.meChatContinue,"",text,"")) //데이터에 추가
            main_recyler.smoothScrollToPosition(0) //맨 아래로 이동
            main_edit.setText("") //입력창 초기화
        }
    }
}
