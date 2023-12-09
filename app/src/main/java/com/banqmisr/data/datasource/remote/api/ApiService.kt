package com.banqmisr.data.datasource.remote.api

import com.banqmisr.data.datasource.remote.api.dto.LatestCurrencyDTO
import com.banqmisr.utils.EndPoint
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(EndPoint.GET_ALL_CURRENCY)
    suspend fun getLatestCurrency(
        @Query("access_key") key: String = "ad7abb74d2f91b2cb0c78156168fb98e",
        @Query("format") format: Int = 1
    ): LatestCurrencyDTO

    @GET("{date}")
    suspend fun getHistoricalDate(
        @Path("date") date: String,
        @Query("access_key") key: String = "ad7abb74d2f91b2cb0c78156168fb98e",
        @Query("symbols") symbols: String,
    ): LatestCurrencyDTO

}