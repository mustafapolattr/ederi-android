package com.mustafa.ederi.core.network

import com.google.common.truth.Truth.assertThat
import com.mustafa.ederi.core.security.SessionManager
import com.mustafa.ederi.core.security.TokenStorage
import com.mustafa.ederi.data.remote.AuthApiService
import com.mustafa.ederi.data.remote.dto.TokenPairDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Exercises [TokenAuthenticator] through a real OkHttpClient + MockWebServer
 * so the retry mechanics match exactly what OkHttp does in production,
 * rather than hand-building okhttp3.Response objects.
 */
class TokenAuthenticatorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient
    private lateinit var tokenStorage: TokenStorage
    private lateinit var sessionManager: SessionManager
    private lateinit var authApiService: AuthApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        tokenStorage = mockk(relaxed = true)
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "valid-refresh"

        sessionManager = mockk(relaxed = true)
        authApiService = mockk()

        val authenticator = TokenAuthenticator(authApiService, tokenStorage, sessionManager)
        val authInterceptor = AuthInterceptor(tokenStorage)

        client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(authenticator)
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `401 triggers a refresh and retries with the new access token`() {
        coEvery { authApiService.refresh(any()) } returns Response.success(TokenPairDto("new-access", "new-refresh"))

        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val request = Request.Builder().url(server.url("/accounts/")).build()
        val response = client.newCall(request).execute()

        assertThat(response.code).isEqualTo(200)
        assertThat(server.requestCount).isEqualTo(2)

        server.takeRequest() // the original, failed request
        val retried = server.takeRequest()
        assertThat(retried.getHeader("Authorization")).isEqualTo("Bearer new-access")

        verify { tokenStorage.saveTokens("new-access", "new-refresh") }
        verify(exactly = 0) { sessionManager.setLoggedOut() }
    }

    @Test
    fun `401 with a failed refresh forces logout and surfaces the original 401`() {
        val emptyBody = "".toResponseBody(null)
        coEvery { authApiService.refresh(any()) } returns Response.error(401, emptyBody)

        server.enqueue(MockResponse().setResponseCode(401))

        val request = Request.Builder().url(server.url("/accounts/")).build()
        val response = client.newCall(request).execute()

        assertThat(response.code).isEqualTo(401)
        assertThat(server.requestCount).isEqualTo(1)

        verify { tokenStorage.clear() }
        verify { sessionManager.setLoggedOut() }
    }
}
