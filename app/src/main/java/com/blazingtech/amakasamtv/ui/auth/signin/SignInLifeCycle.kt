package com.blazingtech.amakasamtv.ui.auth.signin


import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData: LiveData<FirebaseUser?>() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val authListener = FirebaseAuth.AuthStateListener{
        value = firebaseAuth.currentUser
    }


    override fun onActive() {
        firebaseAuth.addAuthStateListener { authListener }
    }

    override fun onInactive() {
       firebaseAuth.removeAuthStateListener(authListener)

    }


}