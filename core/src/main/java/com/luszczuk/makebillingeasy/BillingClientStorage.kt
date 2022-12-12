package com.luszczuk.makebillingeasy

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.luszczuk.makebillingeasy.factory.BillingConnectionFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.shareIn

class BillingClientStorage(
    billingFactory: BillingConnectionFactory,
    connectionShareScope: CoroutineScope = ProcessLifecycleOwner.get().lifecycleScope
) {
    private val _purchasesUpdateFlow = MutableSharedFlow<PurchasesUpdate>(extraBufferCapacity = 1)
    val purchasesUpdateFlow: SharedFlow<PurchasesUpdate> = _purchasesUpdateFlow
        .asSharedFlow()

    val connectionFlow = billingFactory
        .createBillingConnectionFlow(FlowPurchasesUpdatedListener(_purchasesUpdateFlow))
        .shareIn(
            scope = connectionShareScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed(replayExpirationMillis = 0)
        )
}