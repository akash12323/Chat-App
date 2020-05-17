package com.example.messangerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messangerapp.R
import com.example.messangerapp.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.users_list.view.*

class SelectUserAdapter(val user:ArrayList<User>):
    RecyclerView.Adapter<SelectUserAdapter.ItemVieHolder>() {

    var onItemClick:((user: User)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVieHolder {
        return ItemVieHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.users_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = user.size

    override fun onBindViewHolder(holder: ItemVieHolder, position: Int) {
        holder.bind(user[position])
    }

    inner class ItemVieHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(user: User){
            itemView.apply {
                username_tv.text = user.username
                Picasso.get().load(user.imageUrl).into(imageView_user)
                setOnClickListener {
                    onItemClick?.invoke(user)
                }
            }
        }
    }
}