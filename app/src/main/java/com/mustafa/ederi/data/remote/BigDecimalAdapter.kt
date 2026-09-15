package com.mustafa.ederi.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal

/**
 * Reads/writes monetary fields as JSON strings — matching DRF's
 * COERCE_DECIMAL_TO_STRING default for DecimalField. Parsing through
 * Double would silently lose precision, which spec §35 forbids.
 */
class BigDecimalAdapter {
    @FromJson
    fun fromJson(value: String): BigDecimal = BigDecimal(value)

    @ToJson
    fun toJson(value: BigDecimal): String = value.toPlainString()
}
