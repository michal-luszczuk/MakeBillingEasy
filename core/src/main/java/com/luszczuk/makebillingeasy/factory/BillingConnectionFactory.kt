package com.luszczuk.makebillingeasy.factory

import com.android.billingclient.api.PurchasesUpdatedListener
import com.luszczuk.makebillingeasy.BillingConnectionResult
import kotlinx.coroutines.flow.Flow

interface BillingConnectionFactory {

    fun createBillingConnectionFlow(
        listener: PurchasesUpdatedListener
    ): Flow<BillingConnectionResult>
}