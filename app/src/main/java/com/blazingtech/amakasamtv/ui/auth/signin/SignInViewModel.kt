package com.blazingtech.amakasamtv.ui.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.blazingtech.amakasamtv.util.AuthenticationSate.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignInViewModel : ViewModel()  {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<FirebaseUser>()
    private val _isSuccessful = MutableLiveData<Boolean>()


    val authState: LiveData<FirebaseUser> = _authState
    val isSuccessful: LiveData<Boolean> = _isSuccessful




    val authenticationSate = FirebaseUserLiveData().map {
        if (it != null){
            AUTHENTICATED
        }else{
            UNAUTHENTICATED
        }
    }


    init {
        setUpFireBaseAuth()
    }




    private fun setUpFireBaseAuth() {
        _authState.value = auth.currentUser
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { user ->
            if (user.isSuccessful) {
                _authState.value = auth.currentUser
            } else {
                _authState.value = null
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }



}