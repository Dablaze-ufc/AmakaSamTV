package com.blazingtech.amakasamtv.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.blazingtech.amakasamtv.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.forgot_password_fragment.*
import timber.log.Timber

class ForgetPasswordDialog : DialogFragment() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.forgot_password_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        buttonCancel.setOnClickListener {
            this.dismiss()
        }

        buttonResetPassword.isEnabled = true
        buttonResetPassword.setOnClickListener {
            if (!validateResetPassword()){
                returnTransition
            }else{
                resetPassword(resetPasswordEmail = textInputFieldForgotPasswordEmail.editText!!.text.toString().trim())
            }
        }


    }
    // reset password link code
    private fun resetPassword(
        resetPasswordEmail: String
    ) {
        showProgressBar()
        textInputFieldForgotPasswordEmail.isEnabled = false
        buttonResetPassword?.isEnabled = false

        auth.sendPasswordResetEmail(resetPasswordEmail).addOnSuccessListener {
            dialogResetEmail()
            this.dismiss()
            Timber.i("reset link was sent")
            removeProgressBarForgotPassword()
            textInputFieldForgotPasswordEmail.isEnabled = true
            buttonResetPassword?.isEnabled = true
            buttonResetPassword?.text = getString(R.string.reset_password)

        }.addOnFailureListener { e ->
            val snackBar: Snackbar = Snackbar
                .make(
                    textInputFieldForgotPasswordEmail,
                    e.message.toString(),
                    Snackbar.LENGTH_LONG
                )
            snackBar.show()

            removeProgressBarForgotPassword()
            textInputFieldForgotPasswordEmail.isEnabled = true
            buttonResetPassword?.isEnabled = true
            buttonResetPassword?.text = getString(R.string.reset_password)
        }
    }

    private fun validateResetPassword(): Boolean {
         val resetPasswordLink = textInputFieldForgotPasswordEmail.editText!!.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            (resetPasswordLink.isEmpty()) -> {
                textInputFieldForgotPasswordEmail.error = "please enter an email!"
                false
            }
            (!resetPasswordLink.matches(emailPattern.toRegex())) -> {
                textInputFieldForgotPasswordEmail.error = "invalid email address!"
                false
            }
            else -> {
                textInputFieldForgotPasswordEmail.error = null
                textInputFieldForgotPasswordEmail.isErrorEnabled = false
                true
            }
        }
    }

    private fun dialogResetEmail() {
        val alertDialog = activity?.let { MaterialAlertDialogBuilder(it) }

        alertDialog?.setTitle("Password reset link Sent")
        alertDialog?.setIcon(R.drawable.ic_reset)
        alertDialog?.setMessage("please check your Email Inbox to reset your \npassword through the link")
        alertDialog?.setPositiveButton(
            "Ok"
        ) { _, _ -> }
        alertDialog?.show()
    }

    private fun showProgressBar() {
        Timber.i("setUpProgressBarForgotPassword: Started")
        progressBarForgotPassword?.visibility = View.VISIBLE
    }


    // Removed progress bar forgot password
    private fun removeProgressBarForgotPassword() {
        Timber.i("removedProgressBarForgotPassword: gone")
        progressBarForgotPassword?.visibility = View.GONE
    }

}