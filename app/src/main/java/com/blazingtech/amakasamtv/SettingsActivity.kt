package com.blazingtech.amakasamtv

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.provider.Browser
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        fragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        signOutDialogQuestion()
    }

    companion object{
        class SettingsFragment : PreferenceFragment(){
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addPreferencesFromResource(R.xml.root_preferences)


            }
        }
    }

    fun signOutDialogQuestion(): Boolean {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setIcon(R.drawable.ic_sign_out)
        builder.setTitle("Sign Out")
        builder.setMessage("Are you Sure you want to Sign Out?")
        builder.setPositiveButton(
            "yes"
        ) { dialog, which ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton(
            "No"
        ) { dialog, which -> }
        builder.show()

        return true
    }
}