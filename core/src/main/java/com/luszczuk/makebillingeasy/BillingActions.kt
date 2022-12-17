package com.luszczuk.makebillingeasy

import android.app.Activity
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams

interface BillingActions {

    @AnyThread
    suspend fun isFeatureSupported(@BillingClient.FeatureType feature: String): Boolean

    @AnyThread
    suspend fun queryPurchases(params: QueryPurchasesParams): List<Purchase>

    @AnyThread
    suspend fun queryPurchaseHistory(params: QueryPurchaseHistoryParams): List<PurchaseHistoryRecord>

    @AnyThread
    suspend fun queryProductDetails(params: QueryProductDetailsParams): List<ProductDetails>

    @AnyThread
    suspend fun consumePurchase(params: ConsumeParams): String?

    @AnyThread
    suspend fun acknowledgePurchase(params: AcknowledgePurchaseParams)

    @MainThread
    suspend fun launchFlow(activity: Activity, params: BillingFlowParams)
}