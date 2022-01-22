package com.luszczuk.makebillingeasy

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.luszczuk.makebillingeasy.exception.BillingException

fun BillingResult.isSuccessful(): Boolean {
    return responseCode == BillingClient.BillingResponseCode.OK
}

fun <X> BillingResult.callIfSuccessful(onSuccessCallback: (() -> X)): X {
    return if (isSuccessful()) {
        onSuccessCallback()
    } else {
        throw BillingException.fromResult(this)
    }
}

fun BillingResult.throwIfNotSuccessful() {
    if (!isSuccessful()) {
        throw BillingException.fromResult(this)
    }
}