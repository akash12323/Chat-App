package com.example.messangerapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val uid:String,
    val username:String,
    val imageUrl:String
):Parcelable{
    constructor():this("","","")
}