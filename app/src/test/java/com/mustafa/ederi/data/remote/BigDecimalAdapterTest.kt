package com.mustafa.ederi.data.remote

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import org.junit.Test
import java.math.BigDecimal

class BigDecimalAdapterTest {

    private val moshi = Moshi.Builder().add(BigDecimalAdapter()).build()
    private val adapter = moshi.adapter(BigDecimal::class.java)

    @Test
    fun `parses a decimal string without precision loss`() {
        val value = adapter.fromJson("\"1000.10\"")
        assertThat(value).isEqualTo(BigDecimal("1000.10"))
    }

    @Test
    fun `serializes as a plain decimal string`() {
        val json = adapter.toJson(BigDecimal("42.50"))
        assertThat(json).isEqualTo("\"42.50\"")
    }

    @Test
    fun `also accepts a bare JSON number without precision loss`() {
        // Moshi's JsonReader#nextString() returns a number token's raw text
        // rather than routing it through Double, so this stays exact even
        // if a backend endpoint ever renders a decimal as a bare number.
        val value = adapter.fromJson("1000.10")
        assertThat(value).isEqualTo(BigDecimal("1000.10"))
    }
}
