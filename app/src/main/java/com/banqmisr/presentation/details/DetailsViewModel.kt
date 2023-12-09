package com.banqmisr.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.data.datasource.remote.api.network.getSuccessOrFailData
import com.banqmisr.domain.model.HistoricalDataModel
import com.banqmisr.domain.usecase.GetCurrencyByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getCurrencyByDateUseCase: GetCurrencyByDateUseCase
) :
    ViewModel() {

    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _historicalDataStateFlow =
        MutableStateFlow<NetworkState<List<HistoricalDataModel>>>(
            NetworkState.Loading
        )
    val historicalDataStateFlow = _historicalDataStateFlow.asStateFlow()


    private val _dayStateFlow =
        MutableStateFlow<String>("")
    val dayStateFlow = _dayStateFlow.asStateFlow()


    init {
//        getData()
    }

    fun getData(fromCurrency: Pair<String, Double>, symbols: String) {

        val calendar = Calendar.getInstance()

        viewModelScope.launch {

            _dayStateFlow.emit(fromCurrency.first)
            calendar.add(Calendar.DATE, -1)
            var time = dateFormat.format(calendar.time)
            val firstDayDeferred = async {
                getCurrencyByDateUseCase(fromCurrency.second, symbols, date = time)
            }

            calendar.add(Calendar.DATE, -1)
            time = dateFormat.format(calendar.time)
            val secondDayDeferred = async {
                getCurrencyByDateUseCase(fromCurrency.second, symbols, date = time)
            }
            calendar.add(Calendar.DATE, -1)
            time = dateFormat.format(calendar.time)
            val thirdDayDeferred = async {
                getCurrencyByDateUseCase(fromCurrency.second, symbols, date = time)
            }

            val firstDayResponse = firstDayDeferred.await()
            val secondDayResponse = secondDayDeferred.await()
            val thirdDayResponse = thirdDayDeferred.await()


            combine(
                firstDayResponse,
                secondDayResponse,
                thirdDayResponse
            ) { firstDay, secondDay, thirdDay ->

                val list = listOf(firstDay, secondDay, thirdDay)

                if (list.any { it is NetworkState.Loading }) {
                    NetworkState.Loading
                } else if (list.all { it is NetworkState.Error }) {
                    NetworkState.Error("Unkown Error")
                } else {

                    val firstList = firstDay.getSuccessOrFailData() ?: emptyList()
                    val secondList = secondDay.getSuccessOrFailData() ?: emptyList()
                    val thirdList = thirdDay.getSuccessOrFailData() ?: emptyList()

                    val totalList = firstList + secondList + thirdList
                    NetworkState.Success(totalList)
                }
            }.collectLatest {
                _historicalDataStateFlow.emit(it)
            }
        }
    }
}