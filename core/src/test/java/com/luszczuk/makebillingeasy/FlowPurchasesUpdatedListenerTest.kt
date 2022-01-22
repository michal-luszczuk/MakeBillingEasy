package com.luszczuk.makebillingeasy

import app.cash.turbine.test
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class FlowPurchasesUpdatedListenerTest {

    private lateinit var updateSubject: MutableSharedFlow<PurchasesUpdate>
    private lateinit var lisener: FlowPurchasesUpdatedListener

    private val examplePurchasesList = listOf<Purchase>(mockk(), mockk())

    @BeforeEach
    fun setUp() {
        updateSubject = MutableSharedFlow(extraBufferCapacity = 5)
        lisener = FlowPurchasesUpdatedListener(updateSubject)
    }

    @Test
    fun `GIVEN billing result cancelled WHEN onPurchasesUpdated THEN cancel purchase update`() {
        `GIVEN x billing result WHEN onPurchasesUpdated THEN y update emitted`(
            code = BillingClient.BillingResponseCode.USER_CANCELED,
            expectedUpdate = PurchasesUpdate.Canceled(examplePurchasesList)
        )
    }

    @Test
    fun `GIVEN billing result item owned WHEN onPurchasesUpdated THEN already owned purchase update`() {
        `GIVEN x billing result WHEN onPurchasesUpdated THEN y update emitted`(
            code = BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            expectedUpdate = PurchasesUpdate.ItemAlreadyOwned(examplePurchasesList)
        )
    }

    @Test
    fun `GIVEN billing result OK WHEN onPurchasesUpdated THEN success purchase update`() {
        `GIVEN x billing result WHEN onPurchasesUpdated THEN y update emitted`(
            code = BillingClient.BillingResponseCode.OK,
            expectedUpdate = PurchasesUpdate.Success(examplePurchasesList)
        )
    }

    @Test
    fun `GIVEN billing result item unavailable WHEN onPurchasesUpdated THEN unavailable purchase update`() {
        `GIVEN x billing result WHEN onPurchasesUpdated THEN y update emitted`(
            code = BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            expectedUpdate = PurchasesUpdate.ItemUnavailable(examplePurchasesList)
        )
    }

    @Test
    fun `GIVEN billing result feature not supported WHEN onPurchasesUpdated THEN unknown purchase update`() {
        `GIVEN x billing result WHEN onPurchasesUpdated THEN y update emitted`(
            code = BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            expectedUpdate = PurchasesUpdate.UnknownResponse(
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
                examplePurchasesList
            )
        )
    }

    private fun `GIVEN x billing result WHEN onPurchasesUpdated THEN y update emitted`(
        purchases: List<Purchase> = examplePurchasesList,
        @BillingClient.BillingResponseCode code: Int,
        expectedUpdate: PurchasesUpdate
    ) = runTest {
        updateSubject.test {
            //given
            val result = BillingResult
                .newBuilder()
                .setResponseCode(code)
                .build()

            //when
            lisener.onPurchasesUpdated(result, purchases)

            //then
            assertEquals(expectedUpdate, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}