package com.example.socialapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialapp.Dao.postDao
import com.example.socialapp.Dao.userdao
import com.example.socialapp.module.post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_search.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(),OnlikeinSearchclick {
    // TODO: Rename and change types of parameters
    private lateinit var Adapter:SearchAdapter
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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SearchFragment","onViewCreated call")
        conectAdapter()
        sview.setOnClickListener {
            sview.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    Log.d("SearchFragment","onQueryTextSubmit call")
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {



                    Log.d("SearchFragment","onQueryTextChange call")
                    if(p0.toString().trim {it<=' '}.isEmpty()){
                        conectAdapter()
                        Adapter.startListening()
                    }
                    else{
                        postdao=postDao()
                        val postCollection=postdao.collection


                        val query= postCollection.orderBy("potedbyname")
                            .startAt(p0)
                            .endAt(p0+"\uf8ff")

                        val recyclerViewoption=
                            FirestoreRecyclerOptions.Builder<post>().setQuery(query, post::class.java).build()
                        Adapter= SearchAdapter(recyclerViewoption,SearchFragment())
                        search_recyclerView.adapter = Adapter

                        search_recyclerView.layoutManager = LinearLayoutManager(activity);

                        Adapter.startListening()}
                    Log.d("SearchFragment","onQueryTextChange complete")
                    return true
                }

            })
        }
    }
    private fun conectAdapter() {
        Log.d("SearchFragment","connectadapter call")
        postdao=postDao()
        val postCollection=postdao.collection

        val query=postCollection.orderBy("postedAttime", Query.Direction.DESCENDING)
        val recyclerViewoption=
            FirestoreRecyclerOptions.Builder<post>().setQuery(query, post::class.java).build()
        Adapter= SearchAdapter(recyclerViewoption,this)
        search_recyclerView.adapter = Adapter

        search_recyclerView.layoutManager = LinearLayoutManager(activity);
        Log.d("SearchFragment","connectadapter complete")
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("SearchFragment","OnActivityCreated call")

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