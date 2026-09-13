package com.mustafa.ederi.core.config

import com.mustafa.ederi.BuildConfig

/**
 * Thin wrapper around the per-flavor [BuildConfig] fields (spec §62) so
 * callers never reference BuildConfig directly.
 */
object EnvironmentConfig {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL
    val isDebug: Boolean = BuildConfig.DEBUG
}
