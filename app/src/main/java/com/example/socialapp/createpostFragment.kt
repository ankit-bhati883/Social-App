package com.example.socialapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.socialapp.Dao.postDao
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_createpost.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [createpostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class createpostFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var postdao: postDao
    private  var  downloadUri: String=" "
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
        return inflater.inflate(R.layout.fragment_createpost, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout_upload.visibility=View.GONE
        caption_text.visibility=View.VISIBLE
        imagepreview.visibility=View.GONE
        layout.visibility=View.VISIBLE

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        postdao= postDao()

        submit_button.setOnClickListener {

            layout_upload.visibility=View.VISIBLE
            caption_text.visibility=View.GONE
            imagepreview.visibility=View.GONE
            layout.visibility=View.GONE
            Log.d("CreatePost","click on submit button")

            submitpost()




        }


        choose_photo.setOnClickListener {
            Log.d("CreatePost","click on choose photo")

            launchGallery() }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment createpostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            createpostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun launchGallery() {
        Log.d("CreatePost","Gallaery launch")

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            Log.d("CreatePost","get image file data")

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(view?.context?.contentResolver, filePath)
                imagepreview.setImageBitmap(bitmap)
                Log.d("CreatePost","put image in image preview")

                imagepreview.visibility= View.VISIBLE

            } catch (e: IOException) {
                imagepreview.visibility=View.GONE
                e.printStackTrace()
            }
        }
    }
    private fun submitpost() {
        if(filePath != null){
            Log.d("CreatePost","filepath in not empty")

            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            Log.d("CreatePost","ref created")
            val uploadTask = ref?.putFile(filePath!!)
            Log.d("CreatePost","file put in firebase")
            GlobalScope.launch {
                val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        Log.d("CreatePost","task is unsucessful")
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("CreatePost","get downloadUri")

                        downloadUri = task.result.toString()
                        addUploadRecordToDb(downloadUri.toString())
                    } else {
                        // Handle failures
                        Log.d("CreatePost","task is not successful")

                        downloadUri=" "
                    }
                }?.addOnFailureListener{
                    Log.d("CreatePost","Task faliure")

                    downloadUri=" "
                }}
        }else{

            if(caption_text.text.toString().trim{it<=' '}.isNotEmpty()){
                Log.d("CreatePost","text.isNotEmpty ")

                postdao.addpost(caption_text.text.toString().trim{it<=' '}," ")

                finish()
            }
            else{
                Toast.makeText(view?.getContext(), "please upload something ", Toast.LENGTH_SHORT).show()}
            downloadUri=" "
        }}

    private fun finish() {
        val homeFragment = homeFragment()

        this.getFragmentManager()?.beginTransaction()
            ?.replace(R.id.mainframelayout,homeFragment)
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.addToBackStack(null)
            ?.commit()

    }

    override fun setExitTransition(transition: Any?) {

        super.setExitTransition(transition)


    }

    private fun addUploadRecordToDb(imageurl: String) {
        if(downloadUri!=" "){
            if(caption_text.text.toString().trim{it<=' '}.isNotEmpty() && downloadUri.isNotEmpty()){
                Log.d("CreatePost","text.isNotEmpty && downloadUri.isNotEmpty")
//                Toast.makeText(this, "text.isNotEmpty && downloadUri.isNotEmpty", Toast.LENGTH_SHORT).show()
                postdao.addpost(caption_text.text.toString().trim{it<=' '},downloadUri)
                finish()
            }

            else if(downloadUri.isNotEmpty()){
                Log.d("CreatePost","downloadUri.isNotEmpty")
//                Toast.makeText(this, "downloadUri.isNotEmpty", Toast.LENGTH_SHORT).show()
                postdao.addpost(" ",downloadUri)
                finish()
            }}
    }
}