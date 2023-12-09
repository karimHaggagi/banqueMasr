package com.banqmisr.domain.repository

import com.banqmisr.data.datasource.remote.api.dto.LatestCurrencyDTO
import com.banqmisr.data.datasource.remote.api.network.NetworkState
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getLatestCurrency(): Flow<NetworkState<LatestCurrencyDTO>>
}