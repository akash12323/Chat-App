package com.example.messangerapp.adapter

import com.example.messangerapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*

class ChatFromLogAdapter(val text:String,val uri:String) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from_row.text = text
        Picasso.get().load(uri).into(viewHolder.itemView.senders_image)
    }

}