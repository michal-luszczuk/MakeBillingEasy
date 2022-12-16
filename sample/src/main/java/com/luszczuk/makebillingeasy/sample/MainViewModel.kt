package com.luszczuk.makebillingeasy.sample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryPurchasesParams
import com.luszczuk.makebillingeasy.BillingRepository
import com.luszczuk.makebillingeasy.exception.BillingException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val billingRepository: BillingRepository
) : ViewModel() {

    fun onGetPurchasesPressed() {
        viewModelScope.launch {
            try {
                val params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
                val purchases = billingRepository.queryPurchases(params)
                purchases.forEach { purchase ->
                    Log.d("purchase", purchase.toString())
                    // do something with the purchase
                }
            } catch (exception: BillingException) {
                when (exception) {
                    is BillingException.BillingUnavailableException,
                    is BillingException.DeveloperErrorException,
                    is BillingException.FatalErrorException,
                    is BillingException.FeatureNotSupportedException,
                    is BillingException.ItemAlreadyOwnedException,
                    is BillingException.ItemNotOwnedException,
                    is BillingException.ItemUnavailableException,
                    is BillingException.ServiceDisconnectedException,
                    is BillingException.ServiceTimeoutException,
                    is BillingException.ServiceUnavailableException,
                    is BillingException.UnknownException,
                    is BillingException.UserCanceledException -> {
                        Log.d("error", exception.toString())
                    }
                }
            }
        }
    }
}