package com.blazingtech.amakasamtv.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.blazingtech.amakasamtv.R
import com.blazingtech.amakasamtv.ui.auth.register.SignUpFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.sign_up_fragment.*
import timber.log.Timber
import java.util.*

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    private val db = Firebase.firestore
   // private val userRef = db.collection("Users")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        setUpProgressBar()
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //   Log.d(TAG, "${document.id} => ${document.data}")
                    val username = document.getString(SignUpFragment.KEY_USERNAME)
                    val email = document.getString(SignUpFragment.KEY_USER_EMAIL)

                    textView_username.text = username
                    textView_email.text = email
                    removeProgressBar()
                }
            }
            .addOnFailureListener { exception ->
                //  Log.w(TAG, "Error getting documents.", exception)
                removeProgressBar()
            }
    }

    // progress bar set up
    private fun setUpProgressBar() {
        Timber.i("setUpProgressBar: Started")
        progressBarProfile?.visibility = View.VISIBLE
    }

    private fun removeProgressBar() {
        Timber.i("setUpProgressBar: Started")
        progressBarProfile?.visibility = View.GONE
    }
}