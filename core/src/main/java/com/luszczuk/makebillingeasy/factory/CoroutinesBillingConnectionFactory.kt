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
    ): Flow<BillingConnectionResult> = callbackFlow<BillingConnectionResult> {
        val billingClient = billingClientFactory.createBillingClient(context, listener)

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                close(BillingServiceDisconnectedException())
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (isActive) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        trySend(BillingConnectionResult.Success(billingClient))
                    } else {
                        close(BillingException.fromResult(result))
                    }
                } else {
                    billingClient.endConnectionIfConnected()
                }
            }
        })
        awaitClose {
            billingClient.endConnectionIfConnected()
        }
    }.retryWhen { error, _ ->
        error is BillingServiceDisconnectedException
    }.catch { error ->
        emit(convertExceptionIntoErrorResult(error))
    }

    private fun convertExceptionIntoErrorResult(error: Throwable) = BillingConnectionResult.Error(
        exception = if (error is BillingException) {
            error
        } else {
            BillingException.UnknownException(BillingResult())
        }
    )

    private fun BillingClient.endConnectionIfConnected() {
        if (isReady) {
            endConnection()
        }
    }
}
