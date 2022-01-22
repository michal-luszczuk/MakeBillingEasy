package com.luszczuk.makebillingeasy

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult

object BillingResultCreator {

    fun createBillingResult(@BillingClient.BillingResponseCode code: Int) = BillingResult
        .newBuilder()
        .setResponseCode(code)
        .build()

}