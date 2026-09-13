package com.mustafa.ederi.core.network

import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.core.error.AppError
import org.junit.Test

class NetworkResultTest {

    @Test
    fun success_carriesData() {
        val result = NetworkResult.Success("payload")

        assertThat(result.data).isEqualTo("payload")
    }

    @Test
    fun error_carriesHumanReadableMessage() {
        val result = NetworkResult.Error(AppError.NoConnection)

        assertThat(result.error.userMessage).isEqualTo(
            "No internet connection. Please check your network and try again."
        )
    }
}
