package com.luszczuk.makebillingeasy

import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.flow.MutableSharedFlow

class FlowPurchasesUpdatedListener(
    private val updateSubject: MutableSharedFlow<PurchasesUpdate>
) : PurchasesUpdatedListener {

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        updateSubject.tryEmit(convertToUpdate(result, purchases.orEmpty()))
    }

    private fun convertToUpdate(result: BillingResult, purchases: List<Purchase>): PurchasesUpdate =
        when (val responseCode = result.responseCode) {
            BillingResponseCode.OK -> PurchasesUpdate.Success(purchases)
            BillingResponseCode.USER_CANCELED -> PurchasesUpdate.Canceled(purchases)
            BillingResponseCode.ITEM_ALREADY_OWNED -> PurchasesUpdate.ItemAlreadyOwned(purchases)
            BillingResponseCode.ITEM_UNAVAILABLE -> PurchasesUpdate.ItemUnavailable(purchases)
            else -> PurchasesUpdate.UnknownResponse(responseCode, purchases)
        }
}