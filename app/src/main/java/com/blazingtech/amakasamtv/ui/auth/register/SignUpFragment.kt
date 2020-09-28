package com.blazingtech.amakasamtv.ui.auth.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.blazingtech.amakasamtv.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.sign_up_fragment.*
import kotlinx.android.synthetic.main.sign_up_fragment.view.*
import timber.log.Timber

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var viewModel: SignUpViewModel

    // views
    private lateinit var editTextFieldNameSignUp: TextInputLayout
    private lateinit var editTextFieldEmailSignUp: TextInputLayout
    private lateinit var editTextFieldPasswordSignUp: TextInputLayout
    private lateinit var editTextFieldConfirmPasswordSignUp: TextInputLayout

    // strings
    private lateinit var nameSignUp: String
    private lateinit var emailSignUp: String
    private lateinit var passwordSignUp: String

    // firebase
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_up_fragment, container, false)
        auth = FirebaseAuth.getInstance()
        view.apply {
            buttonSignUp?.isEnabled = true
            buttonSignUp?.setOnClickListener {
                if (!initValidateName() || !initValidateEmailSignUp() || !initValidatePasswordSignUp()) {
                    returnTransition
                } else {
                    signUpWithEmailAndPassword(
                        email = emailSignUp,
                        password = passwordSignUp,
                        username = nameSignUp
                    )
                }
            }
        }
        editTextFieldNameSignUp = view.textInputFieldName
        editTextFieldEmailSignUp = view.textInputFieldSignUpEmail
        editTextFieldPasswordSignUp = view.textInputFieldSignUpPassword
        editTextFieldConfirmPasswordSignUp = view.textInputFieldConfirmPassword

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        // TODO: Use the ViewModel
    }

    // validations
    private fun initValidateName(): Boolean {
        nameSignUp = editTextFieldNameSignUp.editText?.text.toString()
        return when {
            (nameSignUp.isEmpty()) -> {
                editTextFieldNameSignUp.error = "please enter a username!"
                editTextFieldNameSignUp.requestFocus()
                false
            }
            (nameSignUp.length < 6) -> {
                editTextFieldNameSignUp.error = "username too short!"
                editTextFieldNameSignUp.requestFocus()
                false
            }
            else -> {
                editTextFieldNameSignUp.error = null
                editTextFieldNameSignUp.isErrorEnabled = false
                true
            }
        }

    }

    private fun initValidateEmailSignUp(): Boolean {
        emailSignUp = editTextFieldEmailSignUp.editText?.text.toString().trim()
        var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            (emailSignUp.isEmpty()) -> {
                editTextFieldEmailSignUp.error = "please enter an email!"
                editTextFieldEmailSignUp.requestFocus()
                false
            }
            (!emailSignUp.matches(emailPattern.toRegex())) -> {
                editTextFieldEmailSignUp.error = "invalid email address"
                false
            }
            else -> {
                editTextFieldEmailSignUp.error = null
                editTextFieldEmailSignUp.isErrorEnabled = false
                true
            }
        }

    }

    private fun initValidatePasswordSignUp(): Boolean {
        passwordSignUp = editTextFieldPasswordSignUp.editText?.text.toString().trim()
        val confirmPasswordSignUp =
            editTextFieldConfirmPasswordSignUp.editText?.text.toString().trim()
        var passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]))"

        return when {
            (passwordSignUp.isEmpty() && confirmPasswordSignUp.isEmpty()) -> {
                editTextFieldPasswordSignUp.error = "please enter a password!"
                editTextFieldConfirmPasswordSignUp.error = "please enter a password!"
                editTextFieldPasswordSignUp.requestFocus()
                false
            }
            (!stringMatches(passwordSignUp, confirmPasswordSignUp)) -> {
                editTextFieldPasswordSignUp.error = "password does'nt match!"
                editTextFieldConfirmPasswordSignUp.error = "password does'nt match!"
                editTextFieldPasswordSignUp.requestFocus()
                false
            }
            (passwordSignUp.length < 6) -> {
                editTextFieldPasswordSignUp.error = "password must be more than 6 characters!"
                editTextFieldPasswordSignUp.requestFocus()
                false
            }
            else -> {
                editTextFieldPasswordSignUp.error = null
                editTextFieldPasswordSignUp.isErrorEnabled = false
                editTextFieldConfirmPasswordSignUp.error = null
                editTextFieldConfirmPasswordSignUp.isErrorEnabled = false
                true
            }
        }

    }

    // string matchers
    private fun stringMatches(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword

    }

    /**
     * --------------setup Firebase Sign up with Email & Password---------------
     */

    private fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ) {
        setUpProgressBar()
        disableAllViews(false)
        activity?.let {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        Timber.i("onComplete: AuthState %s", FirebaseAuth.getInstance().currentUser)

                        //firebase User
                        val firebaseUser: FirebaseUser = auth.currentUser!!
                        val uid = firebaseUser.uid
                        //TODO("FireStore Save Username")

                        sendVerificationCode()
                        removeProgressBar()
                        disableAllViews(true)
                        navigateToLoginFragment(editTextFieldEmailSignUp)
                    } else {
                        val snackBar: Snackbar = Snackbar
                            .make(
                                constraintLayoutSignUp,
                                "Unable to register, ${task.exception.toString()}",
                                Snackbar.LENGTH_LONG
                            )
                            .setAction("Retry") {
                                signUpWithEmailAndPassword(email, password, username)
                            }
                        snackBar.show()
                        removeProgressBar()
                        disableAllViews(true)
                        buttonSignUp?.text = getString(R.string.sign_up)
                    }

                }
        }


    }

    private fun sendVerificationCode() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            activity?.let {
                user.sendEmailVerification().addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        dialogAnswer()
                        Timber.i("dialog should be called in isSuccessful")
                    } else {
                        val snackBar: Snackbar = Snackbar
                            .make(
                                constraintLayoutSignUp,
                                "Could not send Email verification code",
                                Snackbar.LENGTH_LONG
                            )
                            .setAction("Retry") {
                                sendVerificationCode()
                            }
                        snackBar.show()
                    }
                }
            }
        }

    }

    private fun dialogAnswer() {
        val alertDialog = activity?.let { MaterialAlertDialogBuilder(it) }

        alertDialog?.setTitle("Email Verification Sent")
        alertDialog?.setIcon(R.drawable.ic_mail)
        alertDialog?.setMessage("please check your Email Inbox to verify \nand Continue")
        alertDialog?.setPositiveButton(
            "Ok"
        ) { dialog, which -> }
        alertDialog?.show()
    }

    // progress bar set up
    private fun setUpProgressBar() {
       Timber.i("setUpProgressBar: Started")
        progressBarSignUp?.visibility = View.VISIBLE
    }

    private fun removeProgressBar() {
        Timber.i("setUpProgressBar: Started")
        progressBarSignUp?.visibility = View.GONE
    }

    private fun navigateToLoginFragment(v: View) {
        Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_signInFragment)
    }

    private fun disableAllViews(state: Boolean){
        editTextFieldNameSignUp.isEnabled = state
        editTextFieldEmailSignUp.isEnabled = state
        editTextFieldPasswordSignUp.isEnabled = state
        editTextFieldConfirmPasswordSignUp.isEnabled = state
        NestedScrollViewSignUp?.isEnabled = state
        buttonSignUp?.isEnabled = state
        buttonSignUp?.text = ""
    }
}
