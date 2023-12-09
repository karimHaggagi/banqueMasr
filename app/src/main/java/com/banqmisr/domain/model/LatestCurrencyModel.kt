package com.banqmisr.domain.model

import com.banqmisr.data.datasource.remote.api.dto.LatestCurrencyDTO


data class LatestCurrencyModel(
    val date: String = "",
    val ratesNames: Map<String, Double> = hashMapOf(),
    val success: Boolean = false,
)


fun LatestCurrencyDTO.asLatestCurrencyModel(): LatestCurrencyModel {

    return LatestCurrencyModel(
        date = date?:"",
        success = success == true,
        ratesNames = rates?: hashMapOf()
    )
}