package com.mustafa.ederi.core.network

import com.mustafa.ederi.core.error.AppError

/**
 * Consistent success/failure shape for every repository call, per the
 * client-side half of the API design rules in spec §41.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: AppError) : NetworkResult<Nothing>()
}
