package com.blazingtech.amakasamtv.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignInViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<FirebaseUser>()
    private val _isSuccessful = MutableLiveData<Boolean>()

    val authState : LiveData<FirebaseUser> =  _authState
    val isSuccessful : LiveData<Boolean> = _isSuccessful
    


    init {
        setUpFireBaseAuth()
    }


    private fun setUpFireBaseAuth() {
        _authState.value = auth.currentUser
    }

    fun signInWithEmailAndPassword(email: String, password:String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                _authState.value = auth.currentUser
            }
            else{
                _authState.value = null
            }
        }
    }

    fun signOut(){
        auth.signOut()
    }

}