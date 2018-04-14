package com.kyun.testchat

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ChatAdapter : BaseMultiItemQuickAdapter<Chatitem,BaseViewHolder> {

    constructor(items : List<Chatitem>) : super(items) {
        addItemType(Chatitem.inout,R.layout.item_chat_inout)
        addItemType(Chatitem.timeline,R.layout.item_chat_timeline)
        addItemType(Chatitem.meChatStart,R.layout.item_chat_me_start)
        addItemType(Chatitem.meChatContinue,R.layout.item_chat_me_continue)
        addItemType(Chatitem.youChatStart,R.layout.item_chat_you_start)
        addItemType(Chatitem.youChatContinue,R.layout.item_chat_you_continue)
    }

    override fun convert(helper: BaseViewHolder, item: Chatitem) {
        when(helper.itemViewType) {
            Chatitem.meChatStart -> {
                helper.setText(R.id.chat_me_start_contents, item.contents)
                helper.setText(R.id.chat_me_start_left, item.time)
            }
            Chatitem.meChatContinue -> helper.setText(R.id.chat_me_continue_contents,item.contents)
            Chatitem.youChatStart -> {
                helper.setText(R.id.chat_you_start_contents, item.contents)
                helper.setText(R.id.chat_you_name, item.name)
                helper.setText(R.id.chat_you_start_right, item.time)
            }
            Chatitem.youChatContinue -> helper.setText(R.id.chat_you_continue_contents, item.contents)
            Chatitem.inout -> helper.setText(R.id.chat_inout,item.contents)
            Chatitem.timeline -> helper.setText(R.id.chat_timeline,item.contents)
        }
    }

}