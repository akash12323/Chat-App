package com.example.messangerapp.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messangerapp.R
import com.example.messangerapp.adapter.SelectUserAdapter
import com.example.messangerapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    val list = arrayListOf<User>()
    val newList = arrayListOf<User>()
    val selectUserAdapter =
        SelectUserAdapter(list)
    val dbRef = FirebaseAuth.getInstance().uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        setSupportActionBar(toolbar1)

        recyclerView_message.apply {
            layoutManager = LinearLayoutManager(this@NewMessageActivity,RecyclerView.VERTICAL,false)
            adapter = selectUserAdapter
        }
        selectUserAdapter.onItemClick = {
            val i = Intent(this,ChatLogActivity::class.java)
            i.putExtra("USER_KEY",it)
            startActivity(i)
            finish()
        }

        fetchUser()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun fetchUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val user = it.getValue(User::class.java)!!
                    if (!user.uid.equals(dbRef)) {
                        list.add(user)
                    }
                    else{  }
//                    selectUserAdapter.notifyDataSetChanged()
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)

        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        searchView.setQueryHint("Search by Username")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.length>2) {
                    displayTodo(newText.toString())
                }
                else {
                    newList.clear()
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun displayTodo(newText: String) {
        val adapternew = SelectUserAdapter(newList)

        for (`object` in list) {
            if (`object`.username.toLowerCase().contains(newText.toLowerCase())) {
                newList.clear()
                newList.add(`object`)

                recyclerView_message.apply {
                    layoutManager = LinearLayoutManager(this@NewMessageActivity,RecyclerView.VERTICAL,false)
                    adapter = adapternew
                }
                adapternew.notifyDataSetChanged()
                adapternew.onItemClick = {
                    val i = Intent(this,ChatLogActivity::class.java)
                    i.putExtra("USER_KEY",it)
                    startActivity(i)
                    finish()
                }
            }
        }
    }

}
