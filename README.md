## 💪 Motivation
Have you ever tried to integrate Google Play Billing Library to your project?
I did, and every time it was a painful experience... 😢 the need to remember:
- that to do anything we need the valid and connected BillingClient
- what to do after successful connection?
- what in case of the failure?
- what to do about reconnection if something go wrong and client is disconnected?
- what about reusing the same BillingClient, is it safe?

... and at the very end the fight how to use it in our clean architecture/ViewModel/MVVM, how to make the code responsible for payments testable? 😕

All these difficulties motivated me to build this library 🚀.

## 🎯 Goal
Billing Library integration must be fun, easy and clean! **We want to enjoy the monetization of our applications and not suffer during implementation** (treating it as a necessary evil).
**MakeBillingEasy** is a wrapper for Google Play Billing Library to simplify the whole implementation.

## 💡 Solution - MakeBillingEasy library!
### Benefits
- **Easy to use**: You can easily access all BillingClient methods through one simple `BillingRepository` interface. Forget about having to connect and reconnect to the client. Everything will be handled for you!
- **Customizable**: Default implementation is ready to use. But if you need different reconnection policy/behavior you can customize it on your own. The `BillingClient` connection is shared, so you decide how long you wan to keep it open (process/activity lifecycle) or reuse.
- **Clean**: The whole implementation is hidden behind repository which you can use in your ViewModels/UseCases.
- **Lightweight**: The core module and the whole concept is super small so size of your app will not increase.
- **Testable**: This library makes your classes that use billing repository easier to test so you never miss or lose any revenue any more

## 🚚 Download
MakeBillingEasy is available on `mavenCentral()`.

Latest release: [![MavenCentral version](https://img.shields.io/maven-central/v/com.luszczuk.makebillingeasy/core?color=%2300cc00&style=flat-square)](https://central.sonatype.dev/namespace/com.luszczuk.makebillingeasy)
```kotlin
// core module
implementation("com.luszczuk.makebillingeasy:core:{latestRelease}")
```

## 🌟 Quick Start

### 1. Create `BillingRepository`
To use the standard behavior you can easily use the creator object:
```kotlin
val billingRepository : BillingRepository = DefaultEasyBillingRepositoryCreator.createBillingRepository(applicationContext)
```
To use the full potential it is the best to store `BillingRepository` instance as a singleton and inject it to your classes.

You can use Dagger/Hilt for that. Example module that will create and provide `BillingRepository`:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class EasyBillingModule {

    companion object {

        @Provides
        @Singleton
        fun provideBillingRepository(applicationContext: Application): BillingRepository {
            return DefaultEasyBillingRepositoryCreator.createBillingRepository(applicationContext)
        }
    }

    @Binds
    abstract fun provideCoroutineBilling(billingRepository: BillingRepository): BillingConnector

}
```

### 2. Use repository to access available billing api methods
Each of the methods below is kotlin suspending function. In case of any connection error, or billing client response status different than [BillingResponseCode.OK](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.BillingResponseCode#OK) expect that these methods will throw `BillingException`

#### Acknowledge purchase
>under the hood `billingClient.acknowledgePurchase`
```kotlin
 val acknowledgeParams = AcknowledgePurchaseParams
    .newBuilder()
    .setPurchaseToken(purchaseTokenValue)
    .build()

billingRepository.acknowledgePurchase(acknowledgeParams)
```

#### Consume purchase
>under the hood `billingClient.consumeAsync`
```kotlin
val consumeParams = ConsumeParams
    .newBuilder()
    .setPurchaseToken(purchaseTokenValue)
    .build()

billingRepository.consumePurchase(consumeParams)
```

#### Get purchases
>under the hood `billingClient.queryPurchasesAsync`
```kotlin
val params = QueryPurchasesParams
    .newBuilder()
    .setProductType(BillingClient.ProductType.SUBS)

billingRepository.queryPurchases(params)
```

#### Get purchase history
>under the hood `billingClient.queryPurchaseHistoryAsync`
```kotlin
val params = QueryPurchaseHistoryParams
    .newBuilder()
    .setProductType(ProductType.SUBS)
    .build()

billingRepository.queryPurchaseHistory(params)
```

#### Get product details
>under the hood `billingClient.queryProductDetailsAsync`
```kotlin
val productDetailsParamsList = listOf(
    QueryProductDetailsParams.Product.newBuilder()
        .setProductType(BillingClient.ProductType.SUBS)
        .setProductId(productId)
        .build()
)

val params = QueryProductDetailsParams.newBuilder()
    .setProductList(productDetailsParamsList)
    .build()

billingRepository.queryProductDetails(params)
```

#### Check if feature is supported
>under the hood `billingClient.isFeatureSupported`
```kotlin
billingRepository.isFeatureSupported(FeatureType.IN_APP_MESSAGING)
```

#### Launch the purchase flow
>under the hood `billingClient.launchBillingFlow`

Observe for the purchase updates in your activity/fragment/viewModel
```kotlin
viewModelScope.launch {
    try {
        billingRepository.observePurchaseUpdates().collect { update ->
            // handle purchase update state change
        }
    } catch (exception: Exception) {
        // ignore
    }
}
```
Later start the purchase flow in the activity/fragment
```kotlin
billingRepository.launchFlow(activity, params)
```

### 3. Error handling
While using Billing Library we have to handle and check every time `billingResult.responseCode`.
This library uses exceptions to inform in case of failure. `BillingException` is the base sealed class for different types exceptions.
Base on the exception type we can inform user accordingly.

Example how to handle an error (example taken from example ViewModel implementation):
```kotlin
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
```


<br><br>


## 🧠 Concept & Design
<details>
<summary>Open to understand more the concept behind this library</summary>
<br>
            
TBA

</details>
