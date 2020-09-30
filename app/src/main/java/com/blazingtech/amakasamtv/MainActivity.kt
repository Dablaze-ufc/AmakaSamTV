package com.blazingtech.amakasamtv

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


       /* user = FirebaseAuth.getInstance().currentUser!!
        if(user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }*/

    }
}