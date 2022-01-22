package com.luszczuk.makebillingeasy

import kotlinx.coroutines.flow.Flow

interface BillingConnector {

    fun connectToBilling(): Flow<BillingConnectionResult>
}