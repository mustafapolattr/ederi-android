package com.mustafa.ederi.core.error

import com.mustafa.ederi.core.network.NetworkResult
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/** Mirrors the §41 error envelope: {"success": false, "error": {"code","message","field"}}. */
@JsonClass(generateAdapter = true)
internal data class ErrorEnvelopeDto(
    val success: Boolean,
    val error: ErrorDetailDto?
)

@JsonClass(generateAdapter = true)
internal data class ErrorDetailDto(
    val code: String,
    val message: String,
    val field: String?
)

/**
 * Turns a failed Retrofit response into an [AppError], falling back to a
 * generic server error when the body doesn't match the §41 shape — never
 * surfaces a raw backend message or stack trace to the UI.
 */
fun Response<*>.toAppError(moshi: Moshi): AppError {
    if (code() == 401) return AppError.Unauthorized

    val detail = errorBody()?.string()?.let { raw ->
        runCatching { moshi.adapter(ErrorEnvelopeDto::class.java).fromJson(raw) }.getOrNull()
    }?.error

    return if (detail != null) {
        AppError.Validation(detail.field, detail.message)
    } else {
        AppError.Server(code())
    }
}

/** Runs a Retrofit call and maps both transport failures and the §41 error envelope into [NetworkResult]. */
suspend fun <T> safeApiCall(moshi: Moshi, call: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = call()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.Error(response.toAppError(moshi))
        }
    } catch (e: SocketTimeoutException) {
        NetworkResult.Error(AppError.Timeout)
    } catch (e: IOException) {
        NetworkResult.Error(AppError.NoConnection)
    } catch (e: Exception) {
        NetworkResult.Error(AppError.Unknown(e))
    }
}

/** Variant of [safeApiCall] for endpoints that return 204 No Content (no body to unwrap). */
suspend fun safeUnitApiCall(moshi: Moshi, call: suspend () -> Response<Unit>): NetworkResult<Unit> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(response.toAppError(moshi))
        }
    } catch (e: SocketTimeoutException) {
        NetworkResult.Error(AppError.Timeout)
    } catch (e: IOException) {
        NetworkResult.Error(AppError.NoConnection)
    } catch (e: Exception) {
        NetworkResult.Error(AppError.Unknown(e))
    }
}
