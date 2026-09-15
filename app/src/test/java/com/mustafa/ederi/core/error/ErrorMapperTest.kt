package com.mustafa.ederi.core.error

import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.core.network.NetworkResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorMapperTest {

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Test
    fun `successful response maps to Success`() = runTest {
        val result = safeApiCall(moshi) { Response.success("payload") }
        assertThat(result).isEqualTo(NetworkResult.Success("payload"))
    }

    @Test
    fun `401 maps to Unauthorized regardless of body shape`() = runTest {
        val body = "{}".toResponseBody("application/json".toMediaType())
        val result = safeApiCall(moshi) { Response.error<String>(401, body) }
        assertThat((result as NetworkResult.Error).error).isEqualTo(AppError.Unauthorized)
    }

    @Test
    fun `spec 41 error envelope maps to Validation with field and message`() = runTest {
        val json = """{"success":false,"error":{"code":"VALIDATION_ERROR","message":"Invalid transaction amount.","field":"amount"}}"""
        val body = json.toResponseBody("application/json".toMediaType())
        val result = safeApiCall(moshi) { Response.error<String>(400, body) }
        val error = (result as NetworkResult.Error).error as AppError.Validation
        assertThat(error.field).isEqualTo("amount")
        assertThat(error.reason).isEqualTo("Invalid transaction amount.")
    }

    @Test
    fun `unparseable 500 body falls back to a generic Server error`() = runTest {
        val body = "not json".toResponseBody("text/plain".toMediaType())
        val result = safeApiCall(moshi) { Response.error<String>(500, body) }
        assertThat((result as NetworkResult.Error).error).isEqualTo(AppError.Server(500))
    }

    @Test
    fun `SocketTimeoutException maps to Timeout`() = runTest {
        val result = safeApiCall<String>(moshi) { throw SocketTimeoutException() }
        assertThat((result as NetworkResult.Error).error).isEqualTo(AppError.Timeout)
    }

    @Test
    fun `IOException maps to NoConnection`() = runTest {
        val result = safeApiCall<String>(moshi) { throw IOException() }
        assertThat((result as NetworkResult.Error).error).isEqualTo(AppError.NoConnection)
    }

    @Test
    fun `204 No Content maps to Success via safeUnitApiCall`() = runTest {
        val result = safeUnitApiCall(moshi) { Response.success<Unit>(204, null) }
        assertThat(result).isEqualTo(NetworkResult.Success(Unit))
    }
}
