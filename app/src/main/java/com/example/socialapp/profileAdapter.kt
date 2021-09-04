package com.example.socialapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.module.post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.postview.view.*

class profileAdapter(options: FirestoreRecyclerOptions<post>,private val listener: profileFragment) : FirestoreRecyclerAdapter<post, profileAdapter.profileViewHolder>(
    options
) {
    class profileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimage=itemView.user_image
        val username=itemView.user_name
        val postedat=itemView.postedAt
        val caption=itemView.caption
        val likeimage=itemView.likebutton
        val likecount=itemView.likecount
        val imagepost=itemView.post_image

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): profileViewHolder {
        val viewholder=profileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.postview, parent, false)
        )
        viewholder.likeimage.setOnClickListener {
            listener.onlikebuttonClick(snapshots.getSnapshot(viewholder.adapterPosition).id)
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: profileViewHolder, position: Int, model: post) {
        holder.username.text=model.postedby.displayNames
        if(model.caption!=" "){
            holder.caption.text=model.caption
            holder.caption.visibility=View.VISIBLE
        }
        else{
            holder.caption.visibility=View.GONE
        }

        holder.likecount.text=model.likedby.size.toString()
        Glide.with(holder.userimage).load(model.postedby.imageurl).circleCrop().into(holder.userimage)
        holder.postedat.text=Utils.getTimeAgo(model.postedAttime)
        if(model.postimageurl!=" "){
            Glide.with(holder.imagepost).load(model.postimageurl).into(holder.imagepost)
            holder.imagepost.visibility=View.VISIBLE
        }
        else{
            holder.imagepost.visibility=View.GONE
        }
        val auth= Firebase.auth
        val currentuserid=auth.currentUser!!.uid
        val isliked=model.likedby.contains(currentuserid)

        if(isliked){
            holder.likeimage.setImageDrawable(ContextCompat.getDrawable(holder.likeimage.context,R.drawable.ic_liked))
        }
        else{
            holder.likeimage.setImageDrawable(ContextCompat.getDrawable(holder.likeimage.context,R.drawable.ic_unliked))
        }




    }
    }

interface Onlikedclick{
    fun onlikebuttonClick(id:String)
}