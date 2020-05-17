package com.example.messangerapp.models

data class ChatMessage(
    val id:String,
    val text:String,
    val fromid:String,
    val toid:String,
    val timestamp:Long
){
    constructor():this("","","","",0L)
}