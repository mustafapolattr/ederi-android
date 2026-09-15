package com.mustafa.ederi.core.di

import com.mustafa.ederi.core.config.EnvironmentConfig
import com.mustafa.ederi.core.network.AuthInterceptor
import com.mustafa.ederi.core.network.TokenAuthenticator
import com.mustafa.ederi.data.remote.ApiService
import com.mustafa.ederi.data.remote.AuthApiService
import com.mustafa.ederi.data.remote.BigDecimalAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(BigDecimalAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun loggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (EnvironmentConfig.isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @AuthClient
    @Provides
    @Singleton
    fun provideAuthOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .build()

    @AuthenticatedClient
    @Provides
    @Singleton
    fun provideAuthenticatedOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor())
            .build()

    @AuthClient
    @Provides
    @Singleton
    fun provideAuthRetrofit(@AuthClient okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(EnvironmentConfig.apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @AuthenticatedClient
    @Provides
    @Singleton
    fun provideAuthenticatedRetrofit(@AuthenticatedClient okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(EnvironmentConfig.apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(@AuthClient retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideApiService(@AuthenticatedClient retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
