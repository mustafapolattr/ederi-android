package com.mustafa.ederi.core.error

/**
 * Every error a repository can surface to the presentation layer, already
 * mapped away from raw HTTP/IO exceptions so screens never show a raw
 * backend error message (spec §70).
 */
sealed class AppError(val userMessage: String) {
    data object NoConnection : AppError("No internet connection. Please check your network and try again.")
    data object Unauthorized : AppError("Your session has expired. Please sign in again.")
    data object Timeout : AppError("The request took too long. Please try again.")
    data class Validation(val field: String?, val reason: String) :
        AppError(reason)
    data class Server(val code: Int) :
        AppError("Something went wrong on our end. Please try again shortly.")
    data class Unknown(val cause: Throwable) :
        AppError("Something went wrong. Please try again.")
}
