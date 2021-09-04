package com.example.socialapp.module

import java.time.format.SignStyle

data class post(
    val caption:String=" ",
    val postedby: user=user(),
    val postedbyuid:String=" ",
    val potedbyname:String=" ",
    val postedAttime:Long=2L,
    val postimageurl:String=" ",
    val likedby:ArrayList<String> = ArrayList()
)
