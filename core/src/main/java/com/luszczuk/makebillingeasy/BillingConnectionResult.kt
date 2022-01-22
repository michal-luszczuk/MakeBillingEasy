package com.luszczuk.makebillingeasy

import com.android.billingclient.api.BillingClient
import com.luszczuk.makebillingeasy.exception.BillingException

sealed class BillingConnectionResult {
    data class Success(val client: BillingClient) : BillingConnectionResult()
    data class Error(val exception: BillingException) : BillingConnectionResult()
}