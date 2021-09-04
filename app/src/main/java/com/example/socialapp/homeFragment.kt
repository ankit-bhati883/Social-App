package com.example.socialapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.Dao.postDao
import com.example.socialapp.module.post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class homeFragment : Fragment(),Onlikeclick  {
    // TODO: Rename and change types of parameters
    private lateinit var Adapter:Postadapter
    private lateinit var postdao: postDao
    private var param1: String? = null
    private var param2: String? = null

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

        return inflater.inflate(R.layout.fragment_home, container, false)
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
         * @return A new instance of fragment homeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun conectAdapter() {
        postdao=postDao()
        val postCollection=postdao.collection

        val query=postCollection.orderBy("postedAttime", Query.Direction.DESCENDING)
        val recyclerViewoption=
            FirestoreRecyclerOptions.Builder<post>().setQuery(query, post::class.java).build()
        Adapter= Postadapter(recyclerViewoption,this)
        my_recyclerview.adapter = Adapter
//        my_recyclerview.layoutManager= LinearLayoutManager(this)
        my_recyclerview.layoutManager = LinearLayoutManager(activity);
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