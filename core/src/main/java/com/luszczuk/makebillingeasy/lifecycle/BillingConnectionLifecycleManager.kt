package com.luszczuk.makebillingeasy.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.luszczuk.makebillingeasy.BillingConnector
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BillingConnectionLifecycleManager(
    private val connectable: BillingConnector
) : DefaultLifecycleObserver, CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main + CoroutineExceptionHandler { _, _ ->
            // if you want to handle/log connection exceptions here
        }


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        launch {
            connectable.connectToBilling().collect {
                // just holding the connection to the billing client
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        coroutineContext.cancelChildren()
    }
}
