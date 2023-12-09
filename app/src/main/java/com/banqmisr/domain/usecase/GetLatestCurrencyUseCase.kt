package com.banqmisr.domain.usecase

import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.data.datasource.remote.api.network.mapResultTo
import com.banqmisr.domain.model.LatestCurrencyModel
import com.banqmisr.domain.model.asLatestCurrencyModel
import com.banqmisr.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLatestCurrencyUseCase @Inject constructor(private val homeRepository: HomeRepository) {

    suspend operator fun invoke(): Flow<NetworkState<LatestCurrencyModel>> {
        return homeRepository.getLatestCurrency().mapResultTo { latestCurrencyDTO ->
            if (latestCurrencyDTO.success == true) {
                latestCurrencyDTO.asLatestCurrencyModel()
            } else {
                LatestCurrencyModel()
            }
        }
    }
}