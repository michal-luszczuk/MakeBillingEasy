package com.luszczuk.makebillingeasy.factory

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener

class DefaultBillingClientFactory : BillingClientFactory {

    override fun createBillingClient(
        context: Context,
        listener: PurchasesUpdatedListener
    ): BillingClient = BillingClient
        .newBuilder(context)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .setListener(listener)
        .build()
}