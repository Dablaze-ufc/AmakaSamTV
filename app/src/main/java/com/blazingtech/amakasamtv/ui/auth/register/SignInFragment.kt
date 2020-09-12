package com.blazingtech.amakasamtv.ui.auth.register

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.blazingtech.amakasamtv.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.forgot_password_fragment.*
import kotlinx.android.synthetic.main.forgot_password_fragment.view.*
import kotlinx.android.synthetic.main.forgot_password_fragment.view.buttonResetPassword
import kotlinx.android.synthetic.main.sign_in_fragment.*
import kotlinx.android.synthetic.main.sign_in_fragment.view.*
import timber.log.Timber

class SignInFragment : Fragment() {


    private lateinit var alertDialog: AlertDialog

    private lateinit var viewModel: SignInViewModel
    private lateinit var editTextFieldEmailSignIn: TextInputLayout
    private lateinit var editTextFieldPasswordSignIn: TextInputLayout
    private lateinit var editTextFieldResetPassword: TextInputLayout

    // strings
    private lateinit var emailSignIn: String
    private lateinit var passwordSignIn: String
    private lateinit var resetPasswordLink: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

        setUpFireBaseAuth()
        view.apply {
            textViewSignUp.setOnClickListener {
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


    // Dialog fragment for forgot password
    private fun openDialog() {
        val dialogView =
            LayoutInflater.from(activity).inflate(R.layout.forgot_password_fragment, null)
        editTextFieldResetPassword = dialogView.textInputFieldForgotPasswordEmail
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)

        alertDialog = builder.show()

        dialogView.buttonCancel?.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogView.buttonResetPassword?.isEnabled = true
        dialogView.buttonResetPassword?.setOnClickListener {
            if (!validateResetPassword()) {
                returnTransition
            } else {
                resetPassword(resetPasswordEmail = resetPasswordLink)
            }
        }


    }

    // reset password link code
    private fun resetPassword(
        resetPasswordEmail: String
    ) {
        showProgressBar()
        editTextFieldResetPassword.isEnabled = false
        buttonResetPassword?.isEnabled = false

        auth.sendPasswordResetEmail(resetPasswordEmail).addOnSuccessListener {
            dialogResetEmail()
            alertDialog.dismiss()
            Timber.i("reset link was sent")
            removeProgressBarForgotPassword()
            editTextFieldResetPassword.isEnabled = true
            buttonResetPassword?.isEnabled = true
            buttonResetPassword?.text = getString(R.string.reset_password)

        }.addOnFailureListener { e ->
            val snackBar: Snackbar = Snackbar
                .make(
                    editTextFieldResetPassword,
                    e.message.toString(),
                    Snackbar.LENGTH_LONG
                )
            snackBar.show()

            removeProgressBarForgotPassword()
            editTextFieldResetPassword.isEnabled = true
            buttonResetPassword?.isEnabled = true
            buttonResetPassword?.text = getString(R.string.reset_password)
        }
    }


    /**
     * --------------setup Firebase Sign in with Email & Password---------------
     */

    private fun signInWithEmailAndPassword(
        email: String,
        password: String
    ) {
        activity?.let {
            setUpProgressBar()
            disableAllViews(false)
            viewModel.signInWithEmailAndPassword(email, password)

            viewModel.authState.observe(viewLifecycleOwner, Observer { user ->
                user?.let {
                    if (!isUserVerified(it)) {
                        val snackBar: Snackbar = Snackbar
                            .make(
                                constraintLayoutSignIn,
                                "Your Email has not been verified }",
                                Snackbar.LENGTH_LONG
                            )
                        snackBar.show()
                    }
                    else {
//                          TODO("Navigation")
//                          TODO("Show the appBar and the Bottom navigation")
                        Toast.makeText(activity, "Authentication Successful", Toast.LENGTH_LONG)
                            .show()
                        removeProgressBar()
                        disableAllViews(true)
                        buttonSignIn?.text = getString(R.string.login)}
                }

            })

//                if (auth.currentUser != null) {
//                    if (!isUserVerified(auth.currentUser!!)) {
//
//                    } else {
////                          TODO("Navigation")
////                          TODO("Show the appBar and the Bottom navigation")
//                        Toast.makeText(activity, "Authentication Successful", Toast.LENGTH_LONG)
//                            .show()
//                        removeProgressBar()
//                        disableAllViews(true)
//                        buttonSignIn?.text = getString(R.string.login)
//                    }
//                }

//            }.addOnFailureListener(it) { e ->
//                val snackBar: Snackbar = Snackbar
//                    .make(
//                        constraintLayoutSignIn,
//                        e.message.toString(),
//                        Snackbar.LENGTH_LONG
//                    )
//                snackBar.show()
//                removeProgressBar()
//                disableAllViews(true)
//                buttonSignIn?.text = getString(R.string.login)
//            }

        }
    }

    private fun setUpFireBaseAuth() {
        viewModel.authState.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                if (it.isEmailVerified) {
                    Timber.i("onAuthStateChanged: Authenticated with%s", it.email)
                    Timber.i("onAuthStateChanged: sign_in%s", it.uid)
                } else {
                    val snackBar: Snackbar = Snackbar
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
        passwordSignIn = editTextFieldPasswordSignIn.editText?.text.toString().trim()
        val passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]))"

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

    private fun validateResetPassword(): Boolean {
        resetPasswordLink = editTextFieldResetPassword.editText!!.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            (resetPasswordLink.isEmpty()) -> {
                editTextFieldResetPassword.error = "please enter an email!"
                false
            }
            (!resetPasswordLink.matches(emailPattern.toRegex())) -> {
                editTextFieldResetPassword.error = "invalid email address!"
                false
            }
            else -> {
                editTextFieldResetPassword.error = null
                editTextFieldResetPassword.isErrorEnabled = false
                true
            }
        }
    }

    // verifying if user has check link
    private fun isUserVerified(user: FirebaseUser): Boolean {
        return user.isEmailVerified
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

    // set up progress bar forgot password
    private fun showProgressBar() {
        Timber.i("setUpProgressBarForgotPassword: Started")
        progressBarForgotPassword?.visibility = View.VISIBLE
    }

    // Removed progress bar forgot password
    private fun removeProgressBarForgotPassword() {
        Timber.i("removedProgressBarForgotPassword: gone")
        progressBarForgotPassword?.visibility = View.GONE
    }

    // disable all views
    private fun disableAllViews(state: Boolean) {
        editTextFieldEmailSignIn.isEnabled = state
        editTextFieldPasswordSignIn.isEnabled = state
        relativeLayoutSignIn?.isEnabled = state
        buttonSignIn?.isEnabled = state
        textViewForgotYourPassword?.isEnabled = state
        buttonSignIn?.text = ""
    }

    private fun dialogResetEmail() {
        val alertDialog = activity?.let { MaterialAlertDialogBuilder(it) }

        alertDialog?.setTitle("Password reset link Sent")
        alertDialog?.setIcon(R.drawable.ic_reset)
        alertDialog?.setMessage("please check your Email Inbox to reset your \npassword through the link")
        alertDialog?.setPositiveButton(
            "Ok"
        ) { dialog, which -> }
        alertDialog?.show()
    }
}