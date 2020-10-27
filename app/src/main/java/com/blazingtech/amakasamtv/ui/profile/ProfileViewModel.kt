package com.blazingtech.amakasamtv.ui.profile

import androidx.lifecycle.ViewModel
import com.blazingtech.amakasamtv.R
import com.blazingtech.amakasamtv.constants.Constants
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun setUpUserData(onComplete: (QuerySnapshot) -> Unit, onFailure: (Exception) -> Unit) {

        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                    //   Log.d(TAG, "${document.id} => ${document.data}")
                    onComplete(result)
            }
            .addOnFailureListener { exception ->
                //  Log.w(TAG, "Error getting documents.", exception)
               onFailure(exception)
            }
    }
}