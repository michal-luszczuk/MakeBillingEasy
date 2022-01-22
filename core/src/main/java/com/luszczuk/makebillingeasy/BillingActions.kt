package com.luszczuk.makebillingeasy

import android.app.Activity
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.android.billingclient.api.*

interface BillingActions {

    @AnyThread
    suspend fun isFeatureSupported(@BillingClient.FeatureType feature: String): Boolean

    @AnyThread
    suspend fun getPurchases(@BillingClient.SkuType skuType: String): List<Purchase>

    @AnyThread
    suspend fun getPurchaseHistory(@BillingClient.SkuType skuType: String): List<PurchaseHistoryRecord>

    @AnyThread
    suspend fun getSkuDetails(params: SkuDetailsParams): List<SkuDetails>

    @AnyThread
    suspend fun consumeProduct(params: ConsumeParams)

    @AnyThread
    suspend fun acknowledge(params: AcknowledgePurchaseParams)

    @MainThread
    suspend fun launchFlow(activity: Activity, params: BillingFlowParams)
}