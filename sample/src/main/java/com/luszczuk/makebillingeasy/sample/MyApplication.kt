package com.luszczuk.makebillingeasy.sample

import android.app.Application
import com.luszczuk.makebillingeasy.sample.injection.DaggerMyApplicationComponent
import com.luszczuk.makebillingeasy.sample.injection.MyApplicationComponent

class MyApplication : Application() {

    lateinit var appComponent: MyApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerMyApplicationComponent.factory().create(this)
    }
}