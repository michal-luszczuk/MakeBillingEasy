package com.luszczuk.makebillingeasy

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchaseHistoryResult
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
import com.luszczuk.makebillingeasy.BillingResultCreator.createBillingResult
import com.luszczuk.makebillingeasy.exception.BillingException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class EasyBillingRepositoryTest {

    private companion object {
        private const val BILLING_CLIENT_EXTENSION_FILE =
            "com.android.billingclient.api.BillingClientKotlinKt"

        private const val EXAMPLE_PURCHASE_TOKEN = "exampleToken"
    }

    @MockK
    private lateinit var billingClient: BillingClient

    @MockK
    private lateinit var billingClientStorage: BillingClientStorage
    private lateinit var dispatcher: TestDispatcher
    private lateinit var repository: EasyBillingRepository

    @BeforeEach
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        repository = EasyBillingRepository(
            billingClientStorage = billingClientStorage,
            mainDispatcher = dispatcher
        )

        mockkStatic(BILLING_CLIENT_EXTENSION_FILE)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkStatic(BILLING_CLIENT_EXTENSION_FILE)
    }

    @Test
    fun `GIVEN client storage returns flow WHEN connectToBilling THEN client storage connection flow`() =
        runTest {
            //given
            val sharedFlow = mockk<SharedFlow<BillingConnectionResult>>()
            every { billingClientStorage.connectionFlow } returns sharedFlow

            //when
            val actualResult = repository.connectToBilling()

            //then
            assertEquals(sharedFlow, actualResult)
        }

    @Test
    fun `GIVEN billing is feature supported returns true WHEN isFeatureSupported THEN true`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            every {
                billingClient.isFeatureSupported(FeatureType.IN_APP_MESSAGING)
            } returns createBillingResult(BillingResponseCode.OK)

            //when
            val actualResult = repository.isFeatureSupported(
                FeatureType.IN_APP_MESSAGING
            )

            //then
            assertTrue(actualResult)
        }

    @Test
    fun `GIVEN billing is feature supported returns false WHEN isFeatureSupported THEN false`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            every {
                billingClient.isFeatureSupported(FeatureType.IN_APP_MESSAGING)
            } returns createBillingResult(BillingResponseCode.BILLING_UNAVAILABLE)

            //when
            val actualResult = repository.isFeatureSupported(
                FeatureType.IN_APP_MESSAGING
            )

            //then
            assertFalse(actualResult)
        }

    @Test
    fun `GIVEN client query purchases async returns ok result with purchases WHEN queryPurchases THEN purchases`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()

            val purchases = listOf<Purchase>(mockk(), mockk())
            val result = PurchasesResult(
                billingResult = createBillingResult(BillingResponseCode.OK),
                purchasesList = purchases
            )
            val params = mockk<QueryPurchasesParams>()
            coEvery { billingClient.queryPurchasesAsync(params) } returns result

            //when
            val actualResult = repository.queryPurchases(params)

            //then
            assertEquals(purchases, actualResult)
        }

    @Test
    fun `GIVEN client query purchases async returns service unavailable WHEN queryPurchases THEN exception`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val purchaseResult = PurchasesResult(
                billingResult = createBillingResult(BillingResponseCode.SERVICE_UNAVAILABLE),
                purchasesList = mockk()
            )
            val params = mockk<QueryPurchasesParams>()
            coEvery { billingClient.queryPurchasesAsync(params) } returns purchaseResult

            //when
            val actualResult = kotlin.runCatching {
                repository.queryPurchases(params)
            }

            //then
            assertInstanceOf(
                BillingException.ServiceUnavailableException::class.java,
                actualResult.exceptionOrNull()
            )
        }

    @Test
    fun `GIVEN client query purchase history returns ok result with history records WHEN queryPurchaseHistory THEN history records`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val purchases = listOf<PurchaseHistoryRecord>(mockk(), mockk())
            val result = PurchaseHistoryResult(
                billingResult = createBillingResult(BillingResponseCode.OK),
                purchaseHistoryRecordList = purchases
            )
            val params = mockk<QueryPurchaseHistoryParams>()
            coEvery { billingClient.queryPurchaseHistory(params) } returns result

            //when
            val actualResult = repository.queryPurchaseHistory(params)

            //then
            assertEquals(purchases, actualResult)
        }


    @Test
    fun `GIVEN client query purchases history returns service unavailable WHEN queryPurchaseHistory THEN timeout exception`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()

            val historyRecords = listOf<PurchaseHistoryRecord>(mockk(), mockk())
            val result = PurchaseHistoryResult(
                billingResult = createBillingResult(BillingResponseCode.SERVICE_UNAVAILABLE),
                purchaseHistoryRecordList = historyRecords
            )
            val params = mockk<QueryPurchaseHistoryParams>()
            coEvery { billingClient.queryPurchaseHistory(params) } returns result

            //when
            val actualResult = kotlin.runCatching {
                repository.queryPurchaseHistory(params)
            }

            //then
            assertInstanceOf(
                BillingException.ServiceUnavailableException::class.java,
                actualResult.exceptionOrNull()
            )
        }

    @Test
    fun `GIVEN client query product details returns list with result ok WHEN queryProductDetails THEN list of details`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val params = mockk<QueryProductDetailsParams>()

            val details = listOf<ProductDetails>(mockk(), mockk())
            val result = ProductDetailsResult(
                billingResult = createBillingResult(BillingResponseCode.OK),
                productDetailsList = details
            )
            coEvery { billingClient.queryProductDetails(params) } returns result

            //when
            val actualResult = repository.queryProductDetails(params)

            //then
            assertEquals(details, actualResult)
        }

    @Test
    fun `GIVEN client query product details returns item unavailable result WHEN queryProductDetails THEN item unavailable exception`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val params = mockk<QueryProductDetailsParams>()

            val result = ProductDetailsResult(
                billingResult = createBillingResult(BillingResponseCode.ITEM_UNAVAILABLE),
                productDetailsList = mockk()
            )
            coEvery { billingClient.queryProductDetails(params) } returns result

            //when
            val actualResult = kotlin.runCatching {
                repository.queryProductDetails(params)
            }

            //then
            assertInstanceOf(
                BillingException.ItemUnavailableException::class.java,
                actualResult.exceptionOrNull()
            )
        }

    private suspend fun setStorageConnectionFlowToReturnFlowWithSuccess() {
        val sharedFlow = MutableSharedFlow<BillingConnectionResult>(replay = 1)
        sharedFlow.emit(BillingConnectionResult.Success(billingClient))
        every { billingClientStorage.connectionFlow } returns sharedFlow
    }

    @Test
    fun `GIVEN client consume purchase returns token with result ok WHEN consumePurchase THEN token`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val params = mockk<ConsumeParams>()

            val result = ConsumeResult(
                billingResult = createBillingResult(BillingResponseCode.OK),
                purchaseToken = EXAMPLE_PURCHASE_TOKEN
            )
            coEvery { billingClient.consumePurchase(params) } returns result

            //when
            val actualResult = repository.consumePurchase(params)

            //then
            assertEquals(EXAMPLE_PURCHASE_TOKEN, actualResult)
        }

    @Test
    fun `GIVEN client consume purchase returns item not owned result WHEN consumePurchase THEN item not owned exception`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val params = mockk<ConsumeParams>()

            val result = ConsumeResult(
                billingResult = createBillingResult(BillingResponseCode.ITEM_NOT_OWNED),
                purchaseToken = EXAMPLE_PURCHASE_TOKEN
            )
            coEvery { billingClient.consumePurchase(params) } returns result

            //when
            val actualResult = kotlin.runCatching {
                repository.consumePurchase(params)
            }

            //then
            assertInstanceOf(
                BillingException.ItemNotOwnedException::class.java,
                actualResult.exceptionOrNull()
            )
        }

    @Test
    fun `GIVEN client returns result ok WHEN acknowledgePurchase THEN no exception`() = runTest {
        //given
        setStorageConnectionFlowToReturnFlowWithSuccess()
        val params = mockk<AcknowledgePurchaseParams>()

        val result = createBillingResult(BillingResponseCode.OK)
        coEvery { billingClient.acknowledgePurchase(params) } returns result

        //when
        repository.acknowledgePurchase(params)
    }

    @Test
    fun `GIVEN client returns result item not owned WHEN acknowledgePurchase THEN item not owned exception`() = runTest {
        //given
        setStorageConnectionFlowToReturnFlowWithSuccess()
        val params = mockk<AcknowledgePurchaseParams>()

        val result = createBillingResult(BillingResponseCode.ITEM_NOT_OWNED)
        coEvery { billingClient.acknowledgePurchase(params) } returns result

        //when
        val actualResult = kotlin.runCatching {
            repository.acknowledgePurchase(params)
        }

        //then
        assertInstanceOf(
            BillingException.ItemNotOwnedException::class.java,
            actualResult.exceptionOrNull()
        )
    }
}