package com.mustafa.ederi.core.di

import android.content.Context
import androidx.room.Room
import com.mustafa.ederi.data.local.EderiDatabase
import com.mustafa.ederi.data.local.MIGRATION_1_2
import com.mustafa.ederi.data.local.dao.AccountDao
import com.mustafa.ederi.data.local.dao.AppPreferenceDao
import com.mustafa.ederi.data.local.dao.CategoryDao
import com.mustafa.ederi.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEderiDatabase(@ApplicationContext context: Context): EderiDatabase =
        Room.databaseBuilder(context, EderiDatabase::class.java, "ederi.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideAppPreferenceDao(database: EderiDatabase): AppPreferenceDao = database.appPreferenceDao()

    @Provides
    fun provideAccountDao(database: EderiDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideCategoryDao(database: EderiDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: EderiDatabase): TransactionDao = database.transactionDao()
}
