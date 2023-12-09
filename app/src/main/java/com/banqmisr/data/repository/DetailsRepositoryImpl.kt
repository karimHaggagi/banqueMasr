package com.banqmisr.data.repository

import com.banqmisr.data.datasource.remote.api.ApiService
import com.banqmisr.data.datasource.remote.api.dto.LatestCurrencyDTO
import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.data.datasource.remote.api.network.makeApiCall
import com.banqmisr.domain.repository.DetailsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DetailsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dispatchers: CoroutineDispatcher
) : DetailsRepository {

    override suspend fun getLastThreeDays(
        symbols: String,
        date: String
    ): Flow<NetworkState<LatestCurrencyDTO>> {
        return makeApiCall(dispatchers) {
            apiService.getHistoricalDate(symbols = symbols, date = date)
        }
    }
}