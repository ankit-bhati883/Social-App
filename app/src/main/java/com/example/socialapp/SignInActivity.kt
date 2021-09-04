package com.example.socialapp

import android.content.Intent
import android.icu.util.TimeUnit.values
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.socialapp.Dao.userdao
import com.example.socialapp.module.user
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.chrono.JapaneseEra.values

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG="SignInActivity"
    private lateinit var googleSignInClient:GoogleSignInClient
    private val RC_SIGN_IN=103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        signin_button.setOnClickListener {
            Log.d(TAG,"signinbutton click")
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        Log.d(TAG,"signin() complete")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d(TAG,"task created ")
           handelsignin(task)
        }
    }

    private fun handelsignin(task: Task<GoogleSignInAccount>) {

        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)

        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)

            Log.e(TAG,e.toString())
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG,"firebaseAuth call")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signin_button.visibility=View.GONE
        progreebar.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            val auth=auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user

            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }

    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Log.d(TAG,"updataui")
        if(currentUser!=null){
            val user=user(currentUser.uid,currentUser.displayName!!,currentUser.photoUrl.toString())

            val userdaoo=userdao()
            userdaoo.adduser(user)
            val mainActivityintent=Intent(this,MainActivity::class.java)

            startActivity(mainActivityintent)
            finish()
        }
        else{
            signin_button.visibility=View.VISIBLE
            progreebar.visibility=View.GONE
        }
    }

}