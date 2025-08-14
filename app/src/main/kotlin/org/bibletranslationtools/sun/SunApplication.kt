package org.bibletranslationtools.sun

import android.app.Application
import org.bibletranslationtools.sun.di.initKoin
import org.koin.android.ext.koin.androidContext

class SunApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(applicationContext)
        }
    }
}