package com.blazingtech.amakasamtv.ui.auth.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.blazingtech.amakasamtv.HomeActivity
import com.blazingtech.amakasamtv.R
import com.blazingtech.amakasamtv.ui.auth.register.ForgetPasswordDialog
import com.blazingtech.amakasamtv.util.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.sign_in_fragment.*
import kotlinx.android.synthetic.main.sign_in_fragment.view.*
import timber.log.Timber

class SignInFragment : Fragment() {


    private val viewModel: SignInViewModel by viewModels()
    private lateinit var editTextFieldEmailSignIn: TextInputLayout
    private lateinit var editTextFieldPasswordSignIn: TextInputLayout
    private val customLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this@SignInFragment.requireActivity())
    }
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var authListener: FirebaseAuth.AuthStateListener

    private lateinit var emailSignIn: String
    private lateinit var passwordSignIn: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)
        setupFirebaseAuth()
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

    }

    /**
     * --------------setup Firebase sign in with email and password---------------
     */

    private fun signInWithEmailAndPassword(
        email: String,
        password: String
    ) {
        customLoadingDialog.setUpProgressBar()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!viewModel.isUserVerified(auth.currentUser)) {
                        val snackBar = Snackbar.make(
                            constraintLayoutSignIn,
                            "Please Check Your Email " +
                                    "before Login In",
                            Snackbar.LENGTH_SHORT
                        )
                        snackBar.apply {
                            setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                            show()
                        }
                        customLoadingDialog.removeProgressBar()
                    } else {
                        navigateToMainActivity()
                        customLoadingDialog.removeProgressBar()
                        requireActivity().finish()
                    }
                } else {
                    val snackBar = Snackbar.make(
                        constraintLayoutSignIn,
                        "Unable to Sign in, ${task.exception?.message.toString()}",
                        Snackbar.LENGTH_SHORT
                    )
                    snackBar.apply {
                        setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                        setAction("Retry") {
                            signInWithEmailAndPassword(email, password)
                        }
                        show()
                    }
                    customLoadingDialog.removeProgressBar()
                }
            }
    }


    /**
     * --------------setup Firebase firebase auth---------------
     */

    private fun setupFirebaseAuth() {
        authListener = FirebaseAuth.AuthStateListener {
            val users: FirebaseUser? = auth.currentUser
            if (users != null) {
                if (users.isEmailVerified) {
                    Timber.i(
                        "onAuthStateChanged: Authenticated with%s", users.email
                    )
                    Timber.i(
                        "onAuthStateChanged: sign_in%s", users.uid
                    )
                } else {
                    val snackBar = Snackbar.make(
                        constraintLayoutSignIn,
                        "Please Check Your Email before \nLogin In",
                        Snackbar.LENGTH_SHORT
                    )
                    snackBar.apply {
                        setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                        show()
                    }

                    customLoadingDialog.removeProgressBar()
                    viewModel.signOut()
                }
            } else {
                Timber.i(
                    "onAuthStateChanged: sign_out"
                )
            }
        }
    }


    /**
     * --------------setup navigation---------------
     */

    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext().applicationContext, HomeActivity::class.java))
    }

    /**
     * --------------setup editText validations---------------
     */

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

        return when {
            (passwordSignIn.isEmpty()) -> {
                editTextFieldPasswordSignIn.error = "please enter a password!"
                editTextFieldPasswordSignIn.requestFocus()
                false
            }
            (passwordSignIn.length < 7) -> {
                editTextFieldPasswordSignIn.error = "password must be more than 7 characters!"
                editTextFieldPasswordSignIn.requestFocus()
                false
            }
            (passwordSignIn.length > 16) ->{
                editTextFieldPasswordSignIn.error = "password must not be more than 16 characters!"
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
}