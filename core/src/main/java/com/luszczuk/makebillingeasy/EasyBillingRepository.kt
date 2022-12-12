package com.luszczuk.makebillingeasy

import android.app.Activity
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.withContext

class EasyBillingRepository(
    private val billingClientStorage: BillingClientStorage,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BillingRepository {

    private val connectionFlowable
        get() = billingClientStorage.connectionFlow

    override fun connectToBilling(): SharedFlow<BillingConnectionResult> {
        return connectionFlowable
    }

    override fun observePurchaseUpdates(): Flow<PurchasesUpdate> {
        return connectionFlowable.flatMapConcat {
            billingClientStorage.purchasesUpdateFlow
        }
    }

    @AnyThread
    override suspend fun isFeatureSupported(@FeatureType feature: String): Boolean {
        return connectToClientAndCall {
            it.isFeatureSupported(feature).isSuccessful()
        }
    }

    @AnyThread
    override suspend fun getPurchases(params: QueryPurchasesParams): List<Purchase> {
        return connectToClientAndCall { client ->
            val purchasesResult = client.queryPurchasesAsync(params)
            purchasesResult.billingResult.callIfSuccessful {
                purchasesResult.purchasesList
            }
        }
    }

    @AnyThread
    override suspend fun getPurchaseHistory(params: QueryPurchaseHistoryParams): List<PurchaseHistoryRecord> {
        return connectToClientAndCall { client ->
            val purchasesHistoryResult = client.queryPurchaseHistory(params)
            purchasesHistoryResult.billingResult.callIfSuccessful {
                purchasesHistoryResult.purchaseHistoryRecordList.orEmpty()
            }
        }
    }

    @AnyThread
    override suspend fun getProductDetails(params: QueryProductDetailsParams): List<ProductDetails> {
        return connectToClientAndCall { client ->
            val productDetailsResult = client.queryProductDetails(params)
            productDetailsResult.billingResult.callIfSuccessful {
                productDetailsResult.productDetailsList.orEmpty()
            }
        }
    }

    @AnyThread
    override suspend fun consumeProduct(params: ConsumeParams): String? {
        return connectToClientAndCall { client ->
            val consumeResult = client.consumePurchase(params)
            consumeResult.billingResult.callIfSuccessful {
                consumeResult.purchaseToken
            }
        }
    }

    @AnyThread
    override suspend fun acknowledge(params: AcknowledgePurchaseParams) {
        connectToClientAndCall { client ->
            val acknowledgeResult = client.acknowledgePurchase(params)
            acknowledgeResult.throwIfNotSuccessful()
        }
    }

    @UiThread
    override suspend fun launchFlow(activity: Activity, params: BillingFlowParams) {
        connectToClientAndCall { client ->
            withContext(mainDispatcher) {
                val launchResult = client.launchBillingFlow(activity, params)
                launchResult.throwIfNotSuccessful()
            }
        }
    }

    private suspend fun <X : Any?> connectToClientAndCall(
        onSuccessfulConnection: suspend ((client: BillingClient) -> X)
    ): X {
        return when (val result = connectionFlowable.first()) {
            is BillingConnectionResult.Error -> throw result.exception
            is BillingConnectionResult.Success -> onSuccessfulConnection(result.client)
        }
    }
}
