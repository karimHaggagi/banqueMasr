package com.banqmisr.data.datasource.remote.api.network

sealed class NetworkState<out T> {
    object Loading : NetworkState<Nothing>()
    data class Success<R>(val data: R) : NetworkState<R>()
    data class Error(val error: String?) : NetworkState<Nothing>()
}

fun <T> NetworkState<T>.getSuccessOrFailData(): T? {
    return if (this is NetworkState.Success) {
        this.data
    } else {
        null
    }
}

