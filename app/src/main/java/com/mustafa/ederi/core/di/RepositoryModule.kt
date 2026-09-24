package com.mustafa.ederi.core.di

import com.mustafa.ederi.data.repository.AccountRepositoryImpl
import com.mustafa.ederi.data.repository.AuthRepositoryImpl
import com.mustafa.ederi.data.repository.BudgetRepositoryImpl
import com.mustafa.ederi.data.repository.CategoryRepositoryImpl
import com.mustafa.ederi.data.repository.DashboardRepositoryImpl
import com.mustafa.ederi.data.repository.GoalRepositoryImpl
import com.mustafa.ederi.data.repository.RecurringPaymentRepositoryImpl
import com.mustafa.ederi.data.repository.TransactionRepositoryImpl
import com.mustafa.ederi.domain.repository.AccountRepository
import com.mustafa.ederi.domain.repository.AuthRepository
import com.mustafa.ederi.domain.repository.BudgetRepository
import com.mustafa.ederi.domain.repository.CategoryRepository
import com.mustafa.ederi.domain.repository.DashboardRepository
import com.mustafa.ederi.domain.repository.GoalRepository
import com.mustafa.ederi.domain.repository.RecurringPaymentRepository
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

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindRecurringPaymentRepository(impl: RecurringPaymentRepositoryImpl): RecurringPaymentRepository
}
