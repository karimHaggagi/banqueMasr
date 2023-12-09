package com.banqmisr.domain.usecase

import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.data.datasource.remote.api.network.mapResultTo
import com.banqmisr.domain.model.Currency
import com.banqmisr.domain.model.HistoricalDataModel
import com.banqmisr.domain.repository.DetailsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrencyByDateUseCase @Inject constructor(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val repository: DetailsRepository
) {

    suspend operator fun invoke(fromCurrency: Double, symbols: String, date: String): Flow<NetworkState<MutableList<HistoricalDataModel>>> {

        return repository.getLastThreeDays(symbols, date).mapResultTo { latestCurrencyDTO ->
            val list: MutableList<HistoricalDataModel> = mutableListOf()
            list.add(HistoricalDataModel.HistoricalDataHeaderItem(0, latestCurrencyDTO.date ?: ""))
            latestCurrencyDTO.rates?.toList()?.forEachIndexed { index, item ->
                val result = convertCurrencyUseCase.convertFromCurrencyToAnotherCurrency(
                    1.0,
                    fromCurrency,
                    item.second
                )

                list.add(
                    HistoricalDataModel.HistoricalDataItem(
                        index,
                        Currency(item.first, result)
                    )
                )
            }
            list
        }
    }
}