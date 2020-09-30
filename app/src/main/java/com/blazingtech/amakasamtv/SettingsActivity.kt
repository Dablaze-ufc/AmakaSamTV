package com.blazingtech.amakasamtv

import android.content.Intent
import android.os.Bundle
import android.provider.Browser
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val preference: Preference? = findPreference("sign_out")
           /* preference?.setOnPreferenceClickListener {p ->

            }*/

        }
    }

    private fun signOutDialogQuestion() {
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
            finish()
        }
        builder.setNegativeButton(
            "No"
        ) { dialog, which -> }
        builder.show()
    }
}