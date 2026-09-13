package com.mustafa.ederi.core.config

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EnvironmentConfigTest {

    @Test
    fun apiBaseUrl_isConfiguredAndVersioned() {
        assertThat(EnvironmentConfig.apiBaseUrl).isNotEmpty()
        assertThat(EnvironmentConfig.apiBaseUrl).contains("/api/v1/")
    }
}
