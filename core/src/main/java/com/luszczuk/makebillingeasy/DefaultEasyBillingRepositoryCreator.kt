package com.luszczuk.makebillingeasy

import android.content.Context
import com.luszczuk.makebillingeasy.factory.CoroutinesBillingConnectionFactory

object DefaultEasyBillingRepositoryCreator {

    fun createBillingRepository(applicationContext: Context): BillingRepository = EasyBillingRepository(
        billingClientStorage = BillingClientStorage(
            billingFactory = CoroutinesBillingConnectionFactory(context = applicationContext)
        )
    )
}