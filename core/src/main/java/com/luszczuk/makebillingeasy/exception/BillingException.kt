package com.luszczuk.makebillingeasy.exception

import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult

sealed class BillingException(
    val result: BillingResult
) : Exception("Billing exception, code: ${result.responseCode}, message: ${result.debugMessage}") {

    companion object {
        fun fromResult(result: BillingResult): BillingException {
            return when (result.responseCode) {
                BillingResponseCode.NETWORK_ERROR -> NetworkErrorException(result)
                BillingResponseCode.FEATURE_NOT_SUPPORTED -> FeatureNotSupportedException(result)
                BillingResponseCode.SERVICE_DISCONNECTED -> ServiceDisconnectedException(result)
                BillingResponseCode.USER_CANCELED -> UserCanceledException(result)
                BillingResponseCode.SERVICE_UNAVAILABLE -> ServiceUnavailableException(result)
                BillingResponseCode.BILLING_UNAVAILABLE -> BillingUnavailableException(result)
                BillingResponseCode.ITEM_UNAVAILABLE -> ItemUnavailableException(result)
                BillingResponseCode.DEVELOPER_ERROR -> DeveloperErrorException(result)
                BillingResponseCode.ERROR -> FatalErrorException(result)
                BillingResponseCode.ITEM_ALREADY_OWNED -> ItemAlreadyOwnedException(result)
                BillingResponseCode.ITEM_NOT_OWNED -> ItemNotOwnedException(result)
                else -> UnknownException(result)
            }
        }
    }

    /**
     * A network error occurred during the operation.
     */
    class NetworkErrorException(result: BillingResult) : BillingException(result)

    /**
     * Requested feature is not supported by Play Store on the current device.
     */
    class FeatureNotSupportedException(result: BillingResult) : BillingException(result)

    /**
     * The app is not connected to the Play Store service via the Google Play Billing Library.
     */
    class ServiceDisconnectedException(result: BillingResult) : BillingException(result)

    /**
     * Transaction was canceled by the user.
     */
    class UserCanceledException(result: BillingResult) : BillingException(result)

    /**
     * The service is currently unavailable.
     * Since this state is transient, your app should automatically retry (e.g. with exponential back off) to recover from this error.
     * Be mindful of how long you retry if the retry is happening during a user interaction.
     */
    class ServiceUnavailableException(result: BillingResult) : BillingException(result)

    /**
     * Billing API version is not supported for the type requested.
     */
    class BillingUnavailableException(result: BillingResult) : BillingException(result)

    /**
     * Requested product is not available for purchase.
     */
    class ItemUnavailableException(result: BillingResult) : BillingException(result)

    /**
     * Invalid arguments provided to the API.
     */
    class DeveloperErrorException(result: BillingResult) : BillingException(result)

    /**
     * Fatal error during the API action.
     */
    class FatalErrorException(result: BillingResult) : BillingException(result)

    /**
     * Failure to purchase since item is already owned.
     */
    class ItemAlreadyOwnedException(result: BillingResult) : BillingException(result)

    /**
     * Failure to consume since item is not owned.
     */
    class ItemNotOwnedException(result: BillingResult) : BillingException(result)

    /**
     * Unknown - not defined by the billing docs
     */
    class UnknownException(result: BillingResult) : BillingException(result)
}
