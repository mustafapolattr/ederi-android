package com.mustafa.ederi.data.remote

import com.mustafa.ederi.data.remote.dto.AccountCreateRequestDto
import com.mustafa.ederi.data.remote.dto.AccountDto
import com.mustafa.ederi.data.remote.dto.AccountUpdateRequestDto
import com.mustafa.ederi.data.remote.dto.CategoryCreateRequestDto
import com.mustafa.ederi.data.remote.dto.CategoryDto
import com.mustafa.ederi.data.remote.dto.CategoryUpdateRequestDto
import com.mustafa.ederi.data.remote.dto.PaginatedResponseDto
import com.mustafa.ederi.data.remote.dto.TransactionCreateRequestDto
import com.mustafa.ederi.data.remote.dto.TransactionDto
import com.mustafa.ederi.data.remote.dto.TransactionUpdateRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Authenticated endpoints — attached with the access token via [com.mustafa.ederi.core.network.AuthInterceptor]. */
interface ApiService {

    @GET("accounts/")
    suspend fun getAccounts(@Query("page") page: Int? = null): Response<PaginatedResponseDto<AccountDto>>

    @POST("accounts/")
    suspend fun createAccount(@Body request: AccountCreateRequestDto): Response<AccountDto>

    @PATCH("accounts/{id}/")
    suspend fun updateAccount(@Path("id") id: String, @Body request: AccountUpdateRequestDto): Response<AccountDto>

    /** Soft delete — backend returns the account with is_active=false. */
    @DELETE("accounts/{id}/")
    suspend fun deleteAccount(@Path("id") id: String): Response<AccountDto>

    @GET("categories/")
    suspend fun getCategories(@Query("page") page: Int? = null): Response<PaginatedResponseDto<CategoryDto>>

    @POST("categories/")
    suspend fun createCategory(@Body request: CategoryCreateRequestDto): Response<CategoryDto>

    @PATCH("categories/{id}/")
    suspend fun updateCategory(@Path("id") id: String, @Body request: CategoryUpdateRequestDto): Response<CategoryDto>

    @DELETE("categories/{id}/")
    suspend fun deleteCategory(@Path("id") id: String): Response<Unit>

    @GET("transactions/")
    suspend fun getTransactions(
        @Query("account") accountId: String? = null,
        @Query("category") categoryId: String? = null,
        @Query("type") type: String? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null,
        @Query("page") page: Int? = null
    ): Response<PaginatedResponseDto<TransactionDto>>

    @POST("transactions/")
    suspend fun createTransaction(@Body request: TransactionCreateRequestDto): Response<TransactionDto>

    @PATCH("transactions/{id}/")
    suspend fun updateTransaction(@Path("id") id: String, @Body request: TransactionUpdateRequestDto): Response<TransactionDto>

    @DELETE("transactions/{id}/")
    suspend fun deleteTransaction(@Path("id") id: String): Response<Unit>
}
