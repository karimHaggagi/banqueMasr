package com.banqmisr.data.repository

import com.banqmisr.data.datasource.remote.api.ApiService
import com.banqmisr.data.datasource.remote.api.dto.LatestCurrencyDTO
import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.data.datasource.remote.api.network.makeApiCall
import com.banqmisr.domain.repository.HomeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher,
) : HomeRepository {
    override suspend fun getLatestCurrency(): Flow<NetworkState<LatestCurrencyDTO>> {
        return makeApiCall(dispatcher) {
            apiService.getLatestCurrency()
        }
    }


}