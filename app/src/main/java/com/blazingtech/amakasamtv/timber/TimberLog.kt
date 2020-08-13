package com.blazingtech.amakasamtv.timber

import android.app.Application
import timber.log.Timber

class TimberLog : Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}