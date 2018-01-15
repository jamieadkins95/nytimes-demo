package com.jamieadkins.nytimesdemo

import android.app.Application
import android.support.annotation.StringRes
import timber.log.Timber

class CoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        lateinit var instance: CoreApplication
            private set

        //region Convenience methods
        fun getStringRes(@StringRes resId: Int): String {
            return instance.getString(resId)
        }

        fun getStringRes(@StringRes resId: Int, vararg args: Any?): String {
            return instance.getString(resId, *args)
        }
    }
}
