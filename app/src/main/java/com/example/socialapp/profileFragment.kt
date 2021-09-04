package com.example.socialapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialapp.Dao.postDao
import com.example.socialapp.Dao.userdao
import com.example.socialapp.module.post
import com.example.socialapp.module.user
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profileFragment : Fragment(),Onlikedclick {
    private val auth:FirebaseAuth= Firebase.auth
    val cuser_name=auth.currentUser!!.displayName
    val cuser_url=auth.currentUser!!.photoUrl
    // TODO: Rename and change types of parameters
    private lateinit var Adapter:profileAdapter
    private lateinit var postdao: postDao

    private var param1: String? = null
    private var param2: String? = null
    private var name: String? = null
    private var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conectAdapter()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun conectAdapter() {

        val userid=auth.currentUser!!.uid

            val userdaoo= userdao()



        Glide.with(profile_img.context).load(cuser_url).circleCrop().into(profile_img)
            profile_name.text=cuser_name

        postdao=postDao()
        val postCollection=postdao.collection


        val query=postCollection.whereEqualTo("postedbyuid",userid)

        val recyclerViewoption=
            FirestoreRecyclerOptions.Builder<post>().setQuery(query, post::class.java).build()
        Adapter= profileAdapter(recyclerViewoption,this)
        profile_post_recyclerview.adapter = Adapter

        profile_post_recyclerview.layoutManager = LinearLayoutManager(activity);
    }

    override fun onStart() {
        super.onStart()

        Adapter.startListening()
    }


    override fun onStop() {
        super.onStop()
        Adapter.stopListening()
    }

    override fun onlikebuttonClick(id: String) {
        postdao.updatelike(id)
    }


}