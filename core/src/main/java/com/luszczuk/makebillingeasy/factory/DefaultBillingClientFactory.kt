package com.luszczuk.makebillingeasy.factory

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener

class DefaultBillingClientFactory : BillingClientFactory {

    override fun createBillingClient(
        context: Context,
        listener: PurchasesUpdatedListener
    ): BillingClient = BillingClient
        .newBuilder(context)
        .enablePendingPurchases()
        .setListener(listener)
        .build()
}