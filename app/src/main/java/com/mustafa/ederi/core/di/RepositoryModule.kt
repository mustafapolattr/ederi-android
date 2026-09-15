package com.mustafa.ederi.core.di

import com.mustafa.ederi.data.repository.AccountRepositoryImpl
import com.mustafa.ederi.data.repository.AuthRepositoryImpl
import com.mustafa.ederi.data.repository.CategoryRepositoryImpl
import com.mustafa.ederi.data.repository.TransactionRepositoryImpl
import com.mustafa.ederi.domain.repository.AccountRepository
import com.mustafa.ederi.domain.repository.AuthRepository
import com.mustafa.ederi.domain.repository.CategoryRepository
import com.mustafa.ederi.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
}
