package com.example.messangerapp.adapter

import com.example.messangerapp.R
import com.example.messangerapp.models.ChatMessage
import com.example.messangerapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessageRowAdapter(val chatMessage:ChatMessage) : Item<GroupieViewHolder>(){

    var chatPartnerId:String? = null
    var chatPartnerUser:User? = null

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        viewHolder.itemView.usernamelatest.text = chatMessage.text.toString()
        viewHolder.itemView.messagelatest.text = chatMessage.text

        if (chatMessage.fromid == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toid
        }
        else{
            chatPartnerId = chatMessage.fromid
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)!!
                viewHolder.itemView.usernamelatest.text = chatPartnerUser!!.username
                Picasso.get().load(chatPartnerUser!!.imageUrl).into(viewHolder.itemView.imageviewlatest)
            }

        })
    }

}