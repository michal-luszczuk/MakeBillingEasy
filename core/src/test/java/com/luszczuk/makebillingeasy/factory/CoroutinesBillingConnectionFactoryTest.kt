package com.luszczuk.makebillingeasy.factory

import android.content.Context
import app.cash.turbine.test
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.luszczuk.makebillingeasy.BillingConnectionResult
import com.luszczuk.makebillingeasy.BillingResultCreator.createBillingResult
import com.luszczuk.makebillingeasy.exception.BillingException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
internal class CoroutinesBillingConnectionFactoryTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var billingClientFactory: BillingClientFactory

    @MockK
    private lateinit var listener: PurchasesUpdatedListener

    @InjectMockKs
    private lateinit var factory: CoroutinesBillingConnectionFactory

    @Test
    fun `GIVEN billing client setup finished successfully WHEN createBillingConnectionFlow THEN success connection`() =
        runTest {
            //given
            val client = mockk<BillingClient>(relaxed = true) {
                every {
                    startConnection(any())
                } answers {
                    firstArg<BillingClientStateListener>().onBillingSetupFinished(
                        createBillingResult(BillingClient.BillingResponseCode.OK)
                    )
                } andThenThrows mockk()
            }
            mockCreateBillingClient(client)

            //when
            factory.createBillingConnectionFlow(listener).test {
                //then
                assertEquals(BillingConnectionResult.Success(client), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN billing client setup finished with developer error WHEN createBillingConnectionFlow THEN error result with developer exception`() =
        runTest {
            //given
            val client = mockk<BillingClient>(relaxed = true) {
                every {
                    startConnection(any())
                } answers {
                    firstArg<BillingClientStateListener>().onBillingSetupFinished(
                        createBillingResult(BillingClient.BillingResponseCode.DEVELOPER_ERROR)
                    )
                } andThenThrows mockk()
            }
            mockCreateBillingClient(client)

            //when
            factory.createBillingConnectionFlow(listener).test {
                //then
                val item = awaitItem()
                assertInstanceOf(BillingConnectionResult.Error::class.java, item)
                assertInstanceOf(
                    BillingException.DeveloperErrorException::class.java,
                    (item as BillingConnectionResult.Error).exception
                )

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN billing client disconnected and after retry setup finished successfully WHEN createBillingConnectionFlow THEN success connection`() =
        runTest {
            //given
            val client = mockk<BillingClient>(relaxed = true) {
                every {
                    startConnection(any())
                } answers {
                    firstArg<BillingClientStateListener>().onBillingServiceDisconnected()
                } andThenAnswer {
                    firstArg<BillingClientStateListener>().onBillingSetupFinished(
                        createBillingResult(BillingClient.BillingResponseCode.OK)
                    )
                }
            }
            mockCreateBillingClient(client)

            //when
            factory.createBillingConnectionFlow(listener).test {
                //then
                assertEquals(BillingConnectionResult.Success(client), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    private fun mockCreateBillingClient(result: BillingClient) {
        every { billingClientFactory.createBillingClient(context, listener) } returns result
    }

}