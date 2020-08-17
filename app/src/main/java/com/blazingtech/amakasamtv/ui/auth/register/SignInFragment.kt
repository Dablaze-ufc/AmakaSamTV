package com.blazingtech.amakasamtv.ui.auth.register

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import com.blazingtech.amakasamtv.R
import kotlinx.android.synthetic.main.forgot_password_fragment.*
import kotlinx.android.synthetic.main.forgot_password_fragment.view.*
import kotlinx.android.synthetic.main.sign_in_fragment.*
import timber.log.Timber

class SignInFragment : Fragment() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)
        view.findViewById<TextView>(R.id.textViewSignUp).setOnClickListener {
            Timber.i("TextView is Working")
            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment)
        }
         view.findViewById<TextView>(R.id.textViewForgotYourPassword).setOnClickListener {
             openDialog()
         }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
        // TODO: Use the ViewModel
    }

   private fun openDialog(){
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.forgot_password_fragment, null)
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
        val alertDialog = builder.show()

       dialogView.buttonCancel?.setOnClickListener {
               alertDialog.dismiss()
       }
    }

}