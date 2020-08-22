package com.blazingtech.amakasamtv.ui.auth.register

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.blazingtech.amakasamtv.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.forgot_password_fragment.*
import kotlinx.android.synthetic.main.forgot_password_fragment.view.*
import kotlinx.android.synthetic.main.sign_in_fragment.*
import kotlinx.android.synthetic.main.sign_in_fragment.view.*
import timber.log.Timber

class SignInFragment : Fragment() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    private lateinit var viewModel: SignInViewModel
    private lateinit var editTextFieldEmailSignIn: TextInputLayout
    private lateinit var editTextFieldPasswordSignIn: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)
        view.apply {
            findViewById<TextView>(R.id.textViewSignUp).setOnClickListener {
                Timber.i("TextView is Working")
                Navigation.findNavController(view)
                    .navigate(R.id.action_signInFragment_to_signUpFragment)
            }
            findViewById<TextView>(R.id.textViewForgotYourPassword).setOnClickListener {
                openDialog()
            }
            buttonSignIn?.isEnabled = true
            buttonSignIn?.setOnClickListener {
                if (!initValidateEmailSignIn() || !initValidatePasswordSignIn()) {
                    returnTransition
                } else {
                    Toast.makeText(activity, "complete", Toast.LENGTH_SHORT).show()
                }
            }
        }

        editTextFieldEmailSignIn = view.textInputFieldSignInEmail
        editTextFieldPasswordSignIn = view.textInputFieldSignInPassword
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun openDialog() {
        val dialogView =
            LayoutInflater.from(activity).inflate(R.layout.forgot_password_fragment, null)
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)

        val alertDialog = builder.show()

        dialogView.buttonCancel?.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    // validations
    private fun initValidateEmailSignIn(): Boolean {
        val emailSignIn = editTextFieldEmailSignIn.editText?.text.toString().trim()
        var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            (emailSignIn.isEmpty()) -> {
                editTextFieldEmailSignIn.error = "please enter an email!"
                editTextFieldEmailSignIn.requestFocus()
                false
            }
            (!emailSignIn.matches(emailPattern.toRegex())) -> {
                editTextFieldEmailSignIn.error = "invalid email address"
                false
            }
            else -> {
                editTextFieldEmailSignIn.error = null
                editTextFieldEmailSignIn.isErrorEnabled = false
                true
            }
        }
    }

    private fun initValidatePasswordSignIn(): Boolean {
        val passwordSignIn = editTextFieldPasswordSignIn.editText?.text.toString().trim()
        var passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]))"

        return when {
            (passwordSignIn.isEmpty()) -> {
                editTextFieldPasswordSignIn.error = "please enter a password!"
                editTextFieldPasswordSignIn.requestFocus()
                false
            }
            (passwordSignIn.length < 6) -> {
                editTextFieldPasswordSignIn.error = "password must be more than 6 characters!"
                editTextFieldPasswordSignIn.requestFocus()
                false
            }
            /*(!passwordSignIn.matches(passwordPattern.toRegex())) -> {
                editTextFieldPasswordSignIn.error = "password too weak"
                false
            }*/
            else -> {
                editTextFieldPasswordSignIn.error = null
                editTextFieldEmailSignIn.isErrorEnabled = false
                true
            }
        }
    }
}