package com.blazingtech.amakasamtv.ui.auth.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.blazingtech.amakasamtv.HomeActivity
import com.blazingtech.amakasamtv.R
import com.blazingtech.amakasamtv.ui.auth.register.ForgetPasswordDialog
import com.blazingtech.amakasamtv.util.AuthenticationSate.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.sign_in_fragment.*
import kotlinx.android.synthetic.main.sign_in_fragment.view.*
import timber.log.Timber

class SignInFragment : Fragment() {


    private val viewModel: SignInViewModel by viewModels()
    private lateinit var editTextFieldEmailSignIn: TextInputLayout
    private lateinit var editTextFieldPasswordSignIn: TextInputLayout


    private lateinit var emailSignIn: String
    private lateinit var passwordSignIn: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)
        view.apply {
            textViewSignUp.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_signInFragment_to_signUpFragment)
            }
            findViewById<TextView>(R.id.textViewForgotYourPassword).setOnClickListener {
                ForgetPasswordDialog().show(parentFragmentManager, "Forget password")
            }
            buttonSignIn?.isEnabled = true
            buttonSignIn?.setOnClickListener {
                if (!initValidateEmailSignIn() || !initValidatePasswordSignIn()) {
                    returnTransition
                } else {
                    signInWithEmailAndPassword(
                        email = emailSignIn,
                        password = passwordSignIn
                    )
                }
            }
        }

        editTextFieldEmailSignIn = view.textInputFieldSignInEmail
        editTextFieldPasswordSignIn = view.textInputFieldSignInPassword
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {

            authenticationSate.observe(viewLifecycleOwner, Observer {state ->
                when(state){
                    AUTHENTICATED ->{
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                    }
                    UNAUTHENTICATED -> {
                        Toast.makeText(requireContext(), "Please login", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            })

            authState.observe(viewLifecycleOwner, Observer { user ->

                user?.let {
                    if (it.isEmailVerified) {
                        Timber.i("onAuthStateChanged: Authenticated with%s", it.email)
                        Timber.i("onAuthStateChanged: sign_in%s", it.uid)
                    } else {
                        val snackBar = Snackbar
                            .make(
                                constraintLayoutSignIn,
                                "Please verify your email, before signing in",
                                Snackbar.LENGTH_LONG
                            )
                        removeProgressBar()
                        disableAllViews(true)
                        buttonSignIn?.text = getString(R.string.login)
                        viewModel.signOut()
                        snackBar.show()
                    }
                }
            })

        }
    }

    private fun signInWithEmailAndPassword(
        email: String,
        password: String
    ) {
        activity?.let {
            setUpProgressBar()
            disableAllViews(false)
            viewModel.signInWithEmailAndPassword(email, password)
            viewModel.authState.observe(viewLifecycleOwner, Observer { user ->
                if (!isUserVerified(user)) {
                    val snackBar: Snackbar = Snackbar
                        .make(
                            constraintLayoutSignIn,
                            "Your Email has not been verified ",
                            Snackbar.LENGTH_LONG
                        )
                    snackBar.show()
                } else {
                    Toast.makeText(activity, "Authentication Successful", Toast.LENGTH_LONG)
                        .show()
                    removeProgressBar()
                    disableAllViews(true)
                    buttonSignIn?.text = getString(R.string.login)
                    startActivity(Intent(activity, HomeActivity::class.java))
                    activity?.finish()
                }

            })
        }
    }

    // validations
    private fun initValidateEmailSignIn(): Boolean {
        emailSignIn = editTextFieldEmailSignIn.editText?.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            (emailSignIn.isEmpty()) -> {
                editTextFieldEmailSignIn.error = "please enter an email!"
                editTextFieldEmailSignIn.requestFocus()
                false
            }
            (!(emailSignIn.matches(emailPattern.toRegex()))) -> {
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
        passwordSignIn = editTextFieldPasswordSignIn.editText?.text.toString().trim()
//        val passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]))"

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
            else -> {
                editTextFieldPasswordSignIn.error = null
                editTextFieldEmailSignIn.isErrorEnabled = false
                true
            }
        }
    }


    // verifying if user has check link
    private fun isUserVerified(users: FirebaseUser?): Boolean {
        users?.let {
           return it.isEmailVerified
        }
        return false
    }

    // set up progress bar
    private fun setUpProgressBar() {
        Timber.i("setUpProgressBar: Started")
        progressBarSignIn?.visibility = View.VISIBLE
    }

    // Removed up progress bar
    private fun removeProgressBar() {
        Timber.i("removedProgressBar: gone")
        progressBarSignIn?.visibility = View.GONE
    }

    // disable all views
    private fun disableAllViews(state: Boolean) {
        editTextFieldEmailSignIn.isEnabled = state
        editTextFieldPasswordSignIn.isEnabled = state
        buttonSignIn?.isEnabled = state
        textViewForgotYourPassword?.isEnabled = state
        buttonSignIn?.text = ""
    }

}