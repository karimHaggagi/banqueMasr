package com.banqmisr.data.repository

import com.banqmisr.FakeData
import com.banqmisr.data.datasource.remote.api.dto.LatestCurrencyDTO
import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeHomeDataSource() :
    HomeRepository {
    private var shouldReturnError = false


    fun setShouldReturnError() {
        shouldReturnError = true
    }

    override suspend fun getLatestCurrency(): Flow<NetworkState<LatestCurrencyDTO>> {

        return flow {
            emit(NetworkState.Loading)
            if (!shouldReturnError) {
                emit(
                    NetworkState.Success(FakeData.successDTO)
                )
            } else {
                emit(NetworkState.Error("Error"))
            }
        }
    }


}