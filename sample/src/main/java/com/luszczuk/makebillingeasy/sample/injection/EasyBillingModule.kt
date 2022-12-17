package com.luszczuk.makebillingeasy.sample.injection

import android.app.Application
import com.luszczuk.makebillingeasy.BillingConnector
import com.luszczuk.makebillingeasy.BillingRepository
import com.luszczuk.makebillingeasy.DefaultEasyBillingRepositoryCreator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EasyBillingModule {

    companion object {

        @Provides
        @Singleton
        fun provideBillingRepository(applicationContext: Application): BillingRepository {
            return DefaultEasyBillingRepositoryCreator.createBillingRepository(applicationContext)
        }
    }

    @Binds
    abstract fun provideCoroutineBilling(billingRepository: BillingRepository): BillingConnector

}