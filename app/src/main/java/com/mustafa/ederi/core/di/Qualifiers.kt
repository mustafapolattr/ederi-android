package com.mustafa.ederi.core.di

import javax.inject.Qualifier

/** The unauthenticated OkHttpClient/Retrofit/service used only for register/login/refresh/logout. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient

/** The OkHttpClient/Retrofit/service that attaches the access token and auto-refreshes on 401. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient
