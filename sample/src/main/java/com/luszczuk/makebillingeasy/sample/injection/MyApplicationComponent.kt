package com.luszczuk.makebillingeasy.sample.injection

import android.app.Application
import com.luszczuk.makebillingeasy.sample.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [])
interface MyApplicationComponent {

    fun inject(main: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): MyApplicationComponent
    }
}