package com.banqmisr.data.datasource.remote.api.dto


data class LatestCurrencyDTO(
    val base: String?,
    val date: String?,
    val rates: Map<String, Double>?,
    val success: Boolean?,
    val timestamp: Int?
)