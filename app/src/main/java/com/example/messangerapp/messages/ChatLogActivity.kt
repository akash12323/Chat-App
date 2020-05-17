package com.example.messangerapp.messages

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messangerapp.adapter.ChatFromLogAdapter
import com.example.messangerapp.adapter.ChatToLogAdapter
import com.example.messangerapp.R
import com.example.messangerapp.models.ChatMessage
import com.example.messangerapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var nm:NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        nm = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Simple Notification
//            nm.createNotificationChannel(NotificationChannel("first","default",NotificationManager.IMPORTANCE_DEFAULT))

            //Heads up notification
            val channel = (NotificationChannel("first","default",
                NotificationManager.IMPORTANCE_HIGH))
            channel.apply {
                enableLights(true)
                enableVibration(true)
            }
            nm!!.createNotificationChannel(channel)
        }


        val user = intent.getParcelableExtra<User>("USER_KEY")!!

        setSupportActionBar(toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar2.setTitle(user.username)

        recyclerview_chatlog.apply {
            layoutManager = LinearLayoutManager(this@ChatLogActivity,RecyclerView.VERTICAL,false)
            adapter = this@ChatLogActivity.adapter
        }

        listenForMessage()

        send_button_chatlog.setOnClickListener {
            Log.d("chat","Attempt to send a message......")
            performSendMessage()
            edittext_chatlog.setText("")
        }

    }

    private fun listenForMessage() {

        val fromid = FirebaseAuth.getInstance().uid.toString()

        val user = intent.getParcelableExtra<User>("USER_KEY")!!
        val toid = user.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$toid")

        recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)!!

                if (chatMessage.fromid == FirebaseAuth.getInstance().uid) {
                    val currentUser = LatestMessagesActivity.currentUser
                    adapter.add(
                        ChatFromLogAdapter(
                            chatMessage.text,
                            currentUser!!.imageUrl
                        )
                    )
                    recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)
                }
                else{
                    val touser = intent.getParcelableExtra<User>("USER_KEY")!!
                    adapter.add(
                        ChatToLogAdapter(
                            chatMessage.text,
                            touser
                        )
                    )


//                    val builder =
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                            Notification.Builder(this@ChatLogActivity,"first")
//                        } else{
//                            Notification.Builder(this@ChatLogActivity)
//                                .setPriority(Notification.PRIORITY_MAX)
//                                .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
//                        }
//
//                    val simpleNotification = NotificationCompat.Builder(this@ChatLogActivity,"first")
//                        .setContentTitle("Title")
//                        .setContentText("${edittext_chatlog.text}")
//                        .setSmallIcon(R.drawable.varun)
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .build()
//                    nm!!.notify(1,simpleNotification)


                    recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun performSendMessage() {
//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val user = intent.getParcelableExtra<User>("USER_KEY")!!

        val text = edittext_chatlog.text.toString()
        val fromid = FirebaseAuth.getInstance().uid.toString()
        val toid = user.uid
        val timestamp = System.currentTimeMillis()/1000

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$toid").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toid/$fromid").push()

//        recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)

        reference.setValue(ChatMessage(reference.key.toString(),text,fromid, toid, timestamp))
            .addOnSuccessListener {
                Log.d("chat","Message sent successfully")
                recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(ChatMessage(reference.key.toString(),text,fromid, toid, timestamp))
            .addOnSuccessListener {
                Log.d("chat","Message sent successfully")
//                recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)
            }
//        recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)

        val latestMesageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromid/$toid")
        val latestMesageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toid/$fromid")

        latestMesageRef.setValue(ChatMessage(reference.key.toString(),text,fromid, toid, timestamp))
        latestMesageToRef.setValue(ChatMessage(reference.key.toString(),text,fromid, toid, timestamp))

    }

}
