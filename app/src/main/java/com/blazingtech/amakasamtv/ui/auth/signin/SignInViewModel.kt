package com.blazingtech.amakasamtv.ui.auth.signin

import android.app.PendingIntent.getActivity
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.blazingtech.amakasamtv.util.AuthenticationSate.AUTHENTICATED
import com.blazingtech.amakasamtv.util.AuthenticationSate.UNAUTHENTICATED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber


class SignInViewModel : ViewModel()  {
    private var auth = FirebaseAuth.getInstance()

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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = auth.currentUser
            } else {
               _authState.value = null
            }
        }
    }

    // verifying if user has check link
//    private fun isUserVerified(users: FirebaseUser?): Boolean {
//        users?.let {
//            return it.isEmailVerified
//        }
//        return false
//    }

    fun signOut() {
        auth.signOut()
    }



}