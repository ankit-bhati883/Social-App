package com.example.socialapp.Dao

import com.example.socialapp.module.post
import com.example.socialapp.module.user
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class postDao {

    private val db=FirebaseFirestore.getInstance()
     val collection=db.collection("posts")
//    private lateinit var auth: FirebaseAuth
    private val auth=Firebase.auth

    fun addpost(text: String, downloadUri: String){

        GlobalScope.launch(Dispatchers.IO) {
            val userdaoo=userdao()
            val userid=auth.currentUser!!.uid
            val user1=userdaoo.getuser(userid).await().toObject(user::class.java)!!
            val timing=System.currentTimeMillis()


            val post=post(text, user1,userid,user1.displayNames,timing,downloadUri)
            collection.document().set(post)
        }

    }

    fun getpost(id:String):Task<DocumentSnapshot>{
        return collection.document(id).get()
    }

//    fun function(id:String): user? {
//        var user2=user()
//        GlobalScope.launch {
//            val userdaoo= userdao()
//             user2= userdaoo.getuser(id).await().toObject(user::class.java)!!
//
//        }
//         return user2
//    }
    fun updatelike(id: String){
        GlobalScope.launch {
            val currentpost=getpost(id).await().toObject(post::class.java)
            val currentuserid=auth.currentUser!!.uid
            val isliked=currentpost!!.likedby.contains(currentuserid)

            if(isliked){
                currentpost.likedby.remove(currentuserid)
            }
            else{
                currentpost.likedby.add(currentuserid)
            }

            collection.document(id).set(currentpost)
        }
    }
}