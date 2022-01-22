package com.luszczuk.makebillingeasy

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.luszczuk.makebillingeasy.factory.BillingConnectionFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

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