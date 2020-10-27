package com.blazingtech.amakasamtv.ui.auth.signin

import android.app.PendingIntent.getActivity
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.blazingtech.amakasamtv.util.AuthenticationSate.AUTHENTICATED
import com.blazingtech.amakasamtv.util.AuthenticationSate.UNAUTHENTICATED
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber


class SignInViewModel : ViewModel()  {
    private var auth = FirebaseAuth.getInstance()

    val authenticationSate = FirebaseUserLiveData().map {
        if (it != null){
            AUTHENTICATED
        }else{
            UNAUTHENTICATED
        }
    }


    // verifying if user has check link
    fun isUserVerified(users: FirebaseUser?): Boolean {
        users?.let {
            return it.isEmailVerified
        }
        return false
    }

    fun signOut() {
        auth.signOut()
    }



}