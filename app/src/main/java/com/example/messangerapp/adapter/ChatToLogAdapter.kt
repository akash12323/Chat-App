package com.example.messangerapp.adapter

import com.example.messangerapp.R
import com.example.messangerapp.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToLogAdapter(val text:String,val uri:User) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_to_row.text = text
//        Picasso.get().load(uri.imageUrl).into(viewHolder.itemView.reciever_image)
    }

}