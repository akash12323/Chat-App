package com.example.messangerapp.messages

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messangerapp.R
import com.example.messangerapp.adapter.LatestMessageRowAdapter
import com.example.messangerapp.models.ChatMessage
import com.example.messangerapp.models.User
import com.example.messangerapp.register_login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    val latestMessagesMap = HashMap<String,ChatMessage>()

//    var nm:NotificationManager? = null

    companion object{
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        setSupportActionBar(toolbar)
//        toolbar.title = currentUser!!.username
        toolbar.setOnClickListener {
            Toast.makeText(this,"toolbar clicked",Toast.LENGTH_SHORT).show()
        }

//        nm = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            //Simple Notification
////            nm.createNotificationChannel(NotificationChannel("first","default",NotificationManager.IMPORTANCE_DEFAULT))
//
//            //Heads up notification
//            val channel = (NotificationChannel("first","default",
//                NotificationManager.IMPORTANCE_HIGH))
//            channel.apply {
//                enableLights(true)
//                enableVibration(true)
//            }
//            nm!!.createNotificationChannel(channel)
//        }

        recyclerview_latest_message.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        recyclerview_latest_message.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val i = Intent(view.context,ChatLogActivity::class.java)

            var user = item as LatestMessageRowAdapter

            i.putExtra("USER_KEY",user.chatPartnerUser)
            startActivity(i)
        }

        verifyUserIsLoggedIn()
        listenerForLatestMessages()
//        fetchCurrentUser()
    }


//    listen for every latest message sent to or by the user
//  hashmap is used because if we do not use this every time a new message is sent or received by the user, a new row will be created in LatestMessageActivity
    private fun listenerForLatestMessages() {
        val fromid = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromid")

        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)!!

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessage()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)!!

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessage()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun refreshRecyclerViewMessage() {
        adapter.clear()
        var i = 0
        latestMessagesMap.values.forEach{

//            val builder =
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                    Notification.Builder(this@LatestMessagesActivity,"first")
//                } else{
//                    Notification.Builder(this@LatestMessagesActivity)
//                        .setPriority(Notification.PRIORITY_MAX)
//                        .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
//                }
//
//            val simpleNotification = NotificationCompat.Builder(this@LatestMessagesActivity,"first")
//                .setContentTitle("Title")
//                .setContentText("${it.text}")
//                .setSmallIcon(R.drawable.varun)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .build()
//            nm!!.notify(1,simpleNotification)

            adapter.add(LatestMessageRowAdapter(it))

        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        val firebaseUser = FirebaseAuth.getInstance().uid

        if (firebaseUser == null){
            val i = Intent(this,
                RegisterActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        else{
            fetchCurrentUser()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_new_message ->{
                val i = Intent(this,
                    NewMessageActivity::class.java)
                startActivity(i)
            }
            R.id.menu_sign_out ->{
                AlertDialog.Builder(this)
                    .setTitle("Are You Sure")
                    .setMessage("Do you want to logout ?")
                    .setPositiveButton("YES",{ dialogInterface: DialogInterface, i: Int ->
                        FirebaseAuth.getInstance().signOut()
                        val i = Intent(this,
                            RegisterActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                    })
                    .setNegativeButton("NO",{ dialogInterface: DialogInterface, i: Int -> })
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
