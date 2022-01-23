package com.luszczuk.makebillingeasy.sample.injection

import android.app.Application
import com.luszczuk.makebillingeasy.BillingClientStorage
import com.luszczuk.makebillingeasy.BillingConnector
import com.luszczuk.makebillingeasy.BillingRepository
import com.luszczuk.makebillingeasy.EasyBillingRepository
import com.luszczuk.makebillingeasy.factory.BillingConnectionFactory
import com.luszczuk.makebillingeasy.factory.CoroutinesBillingConnectionFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EasyBillingModule {

    companion object {

        @Provides
        fun provideCoroutinesBillingClientFactory(
            applicationContext: Application
        ): BillingConnectionFactory {
            return CoroutinesBillingConnectionFactory(applicationContext)
        }

        @Provides
        @Singleton
        fun provideBillingClientStorage(factory: BillingConnectionFactory): BillingClientStorage {
            return BillingClientStorage(factory)
        }

        @Provides
        @Singleton
        fun provideCoroutineBilling(billingClientStorage: BillingClientStorage): BillingRepository {
            return EasyBillingRepository(billingClientStorage)
        }
    }

    @Binds
    abstract fun provideCoroutineBilling(billingRepository: BillingRepository): BillingConnector


}