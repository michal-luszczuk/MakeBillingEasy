package com.luszczuk.makebillingeasy

import android.app.Activity
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams

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
    suspend fun consumeProduct(params: ConsumeParams): String?

    @AnyThread
    suspend fun acknowledge(params: AcknowledgePurchaseParams)

    @MainThread
    suspend fun launchFlow(activity: Activity, params: BillingFlowParams)
}