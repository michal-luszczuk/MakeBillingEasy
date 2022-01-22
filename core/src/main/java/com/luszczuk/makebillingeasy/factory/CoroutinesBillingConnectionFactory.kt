package com.luszczuk.makebillingeasy.factory

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.luszczuk.makebillingeasy.BillingConnectionResult
import com.luszczuk.makebillingeasy.exception.BillingException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.isActive

class BillingServiceDisconnectedException : Exception()

class CoroutinesBillingConnectionFactory(
    private val context: Context,
    private val billingClientFactory: BillingClientFactory = DefaultBillingClientFactory()
) : BillingConnectionFactory {

    override fun createBillingConnectionFlow(
        listener: PurchasesUpdatedListener
    ): Flow<BillingConnectionResult> {
        val flow = callbackFlow<BillingConnectionResult> {

            val billingClient = billingClientFactory.createBillingClient(context, listener)

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    close(BillingServiceDisconnectedException())
                }

                override fun onBillingSetupFinished(result: BillingResult) {
                    val responseCode = result.responseCode
                    if (isActive) {
                        if (responseCode == BillingClient.BillingResponseCode.OK) {
                            trySend(BillingConnectionResult.Success(billingClient))
                        } else {
                            close(BillingException.fromResult(result))
                        }
                    } else {
                        if (billingClient.isReady) {
                            billingClient.endConnection()
                        }
                    }
                }
            })
            awaitClose {
                if (billingClient.isReady) {
                    billingClient.endConnection()
                }
            }
        }.retryWhen { error, _ ->
            error is BillingServiceDisconnectedException
        }.catch { error ->
            this.emit(
                BillingConnectionResult.Error(
                    exception = if (error is BillingException) {
                        error
                    } else {
                        BillingException.UnknownException(BillingResult())
                    }
                )
            )
        }

        return flow
    }
}
