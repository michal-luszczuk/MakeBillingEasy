package com.luszczuk.makebillingeasy

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchaseHistoryResult
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResult
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
import com.android.billingclient.api.querySkuDetails
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
                billingClient.isFeatureSupported(FeatureType.IN_APP_ITEMS_ON_VR)
            } returns createBillingResult(BillingResponseCode.OK)

            //when
            val actualResult = repository.isFeatureSupported(
                FeatureType.IN_APP_ITEMS_ON_VR
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
                billingClient.isFeatureSupported(FeatureType.IN_APP_ITEMS_ON_VR)
            } returns createBillingResult(BillingResponseCode.BILLING_UNAVAILABLE)

            //when
            val actualResult = repository.isFeatureSupported(
                FeatureType.IN_APP_ITEMS_ON_VR
            )

            //then
            assertFalse(actualResult)
        }

    @Test
    fun `GIVEN client query purchases async returns ok result with purchases WHEN getPurchases THEN purchases`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()

            val purchases = listOf<Purchase>(mockk(), mockk())
            val result = PurchasesResult(
                billingResult = createBillingResult(BillingResponseCode.OK),
                purchasesList = purchases
            )
            coEvery { billingClient.queryPurchasesAsync(SkuType.SUBS) } returns result

            //when
            val actualResult = repository.getPurchases(SkuType.SUBS)

            //then
            assertEquals(purchases, actualResult)
        }

    @Test
    fun `GIVEN client query purchases async returns service unavailable WHEN getPurchases THEN exception`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val purchaseResult = PurchasesResult(
                billingResult = createBillingResult(BillingResponseCode.SERVICE_UNAVAILABLE),
                purchasesList = mockk()
            )
            coEvery { billingClient.queryPurchasesAsync(SkuType.SUBS) } returns purchaseResult

            //when
            val actualResult = kotlin.runCatching {
                repository.getPurchases(SkuType.SUBS)
            }

            //then
            assertInstanceOf(
                BillingException.ServiceUnavailableException::class.java,
                actualResult.exceptionOrNull()
            )
        }

    @Test
    fun `GIVEN client query purchase history returns ok result with history records WHEN getPurchaseHistory THEN history records`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val purchases = listOf<PurchaseHistoryRecord>(mockk(), mockk())
            val result = PurchaseHistoryResult(
                billingResult = createBillingResult(BillingResponseCode.OK),
                purchaseHistoryRecordList = purchases
            )
            coEvery { billingClient.queryPurchaseHistory(SkuType.SUBS) } returns result

            //when
            val actualResult = repository.getPurchaseHistory(SkuType.SUBS)

            //then
            assertEquals(purchases, actualResult)
        }


    @Test
    fun `GIVEN client query purchases history returns service timeout WHEN getPurchaseHistory THEN timeout exception`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()

            val historyRecords = listOf<PurchaseHistoryRecord>(mockk(), mockk())
            val result = PurchaseHistoryResult(
                billingResult = createBillingResult(BillingResponseCode.SERVICE_TIMEOUT),
                purchaseHistoryRecordList = historyRecords
            )
            coEvery { billingClient.queryPurchaseHistory(SkuType.SUBS) } returns result

            //when
            val actualResult = kotlin.runCatching {
                repository.getPurchaseHistory(SkuType.SUBS)
            }

            //then
            assertInstanceOf(
                BillingException.ServiceTimeoutException::class.java,
                actualResult.exceptionOrNull()
            )
        }

    @Test
    fun `GIVEN client query sku details returns list with result ok WHEN getSkuDetails THEN list of details`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val params = mockk<SkuDetailsParams>()

            val details = listOf<SkuDetails>(mockk(), mockk())
            val result = SkuDetailsResult(
                billingResult = createBillingResult(BillingResponseCode.OK),
                skuDetailsList = details
            )
            coEvery { billingClient.querySkuDetails(params) } returns result

            //when
            val actualResult = repository.getSkuDetails(params)

            //then
            assertEquals(details, actualResult)
        }

    @Test
    fun `GIVEN client query sku details returns item unavailable result WHEN getSkuDetails THEN item unavailable exception`() =
        runTest {
            //given
            setStorageConnectionFlowToReturnFlowWithSuccess()
            val params = mockk<SkuDetailsParams>()

            val result = SkuDetailsResult(
                billingResult = createBillingResult(BillingResponseCode.ITEM_UNAVAILABLE),
                skuDetailsList = mockk()
            )
            coEvery { billingClient.querySkuDetails(params) } returns result

            //when
            val actualResult = kotlin.runCatching {
                repository.getSkuDetails(params)
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
    fun `GIVEN client consume purchase returns token with result ok WHEN consumeProduct THEN token`() =
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
            val actualResult = repository.consumeProduct(params)

            //then
            assertEquals(EXAMPLE_PURCHASE_TOKEN, actualResult)
        }

    @Test
    fun `GIVEN client consume purchase returns item not owned result WHEN consumeProduct THEN item not owned exception`() =
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
                repository.consumeProduct(params)
            }

            //then
            assertInstanceOf(
                BillingException.ItemNotOwnedException::class.java,
                actualResult.exceptionOrNull()
            )
        }

    @Test
    fun `GIVEN client returns result ok WHEN acknowledge THEN no exception`() = runTest {
        //given
        setStorageConnectionFlowToReturnFlowWithSuccess()
        val params = mockk<AcknowledgePurchaseParams>()

        val result = createBillingResult(BillingResponseCode.OK)
        coEvery { billingClient.acknowledgePurchase(params) } returns result

        //when
        repository.acknowledge(params)
    }

    @Test
    fun `GIVEN client returns result item not owned WHEN acknowledge THEN item not owned exception`() = runTest {
        //given
        setStorageConnectionFlowToReturnFlowWithSuccess()
        val params = mockk<AcknowledgePurchaseParams>()

        val result = createBillingResult(BillingResponseCode.ITEM_NOT_OWNED)
        coEvery { billingClient.acknowledgePurchase(params) } returns result

        //when
        val actualResult = kotlin.runCatching {
            repository.acknowledge(params)
        }

        //then
        assertInstanceOf(
            BillingException.ItemNotOwnedException::class.java,
            actualResult.exceptionOrNull()
        )
    }
}