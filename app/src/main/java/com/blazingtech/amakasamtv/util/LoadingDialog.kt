package com.blazingtech.amakasamtv.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import com.blazingtech.amakasamtv.R

class LoadingDialog {
    private lateinit var activity: Activity
    private lateinit var alertDialog: AlertDialog

    constructor(_activity: Activity) {
        activity = _activity
    }

    @SuppressLint("InflateParams")
    fun setUpProgressBar() {
        val builder = AlertDialog.Builder(activity)

        val layoutInflater = activity.layoutInflater
        builder.apply {
            setView(layoutInflater.inflate(R.layout.loading_layout, null))
            setCancelable(false)
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun removeProgressBar() {
        alertDialog.dismiss()
    }
}