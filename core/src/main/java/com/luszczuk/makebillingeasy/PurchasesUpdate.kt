package com.luszczuk.makebillingeasy

import com.android.billingclient.api.Purchase

sealed class PurchasesUpdate {
    abstract val purchases: List<Purchase>

    data class Success(override val purchases: List<Purchase>) : PurchasesUpdate()
    data class ItemAlreadyOwned(override val purchases: List<Purchase>) : PurchasesUpdate()
    data class ItemUnavailable(override val purchases: List<Purchase>) : PurchasesUpdate()
    data class Canceled(override val purchases: List<Purchase>) : PurchasesUpdate()
    data class UnknownResponse(
        val code: Int,
        override val purchases: List<Purchase>
    ) : PurchasesUpdate()
}
