package com.example.socialapp.Dao

import android.util.Log
import com.bumptech.glide.Glide
import com.example.socialapp.module.user
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class userdao {
    private val db=FirebaseFirestore.getInstance()
    private val collection=db.collection("users")
    fun adduser(user: user?){
        user?.let {
            GlobalScope.launch {
               collection.document(user.uid).set(it)
            }
        }
    }
    fun getuser(user_id:String): Task<DocumentSnapshot> {
        return collection.document(user_id).get()
    }
//    fun currentuser(user1: user?){
//        val auth= Firebase.auth
//        val userid=auth.currentUser!!.uid
//        GlobalScope.launch() {
//            val userdaoo= userdao()
//            val user1= userdaoo.getuser(userid).await().toObject(user::class.java)!!
//            Log.d("profile","user1 created")
//
//        }
//    }
}