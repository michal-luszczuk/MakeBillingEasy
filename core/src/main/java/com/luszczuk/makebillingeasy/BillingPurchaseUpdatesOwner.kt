package com.luszczuk.makebillingeasy

import kotlinx.coroutines.flow.Flow

interface BillingPurchaseUpdatesOwner {

    fun observePurchaseUpdates(): Flow<PurchasesUpdate>
}