package com.mustafa.ederi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mustafa.ederi.data.local.dao.AccountDao
import com.mustafa.ederi.data.local.dao.AppPreferenceDao
import com.mustafa.ederi.data.local.dao.CategoryDao
import com.mustafa.ederi.data.local.dao.TransactionDao
import com.mustafa.ederi.data.local.entity.AccountEntity
import com.mustafa.ederi.data.local.entity.AppPreferenceEntity
import com.mustafa.ederi.data.local.entity.CategoryEntity
import com.mustafa.ederi.data.local.entity.TransactionEntity

/**
 * Local Room cache (spec §11). Every schema bump from here on must ship an
 * explicit [Migration] — never fallbackToDestructiveMigration in release
 * builds — using the exported schema JSON under app/schemas as the diff
 * baseline.
 */
@Database(
    entities = [
        AppPreferenceEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class EderiDatabase : RoomDatabase() {
    abstract fun appPreferenceDao(): AppPreferenceDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}

/** Adds the Phase 2 offline cache tables (accounts, categories, transactions). */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS accounts (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                currency TEXT NOT NULL,
                initialBalance TEXT NOT NULL,
                currentBalance TEXT NOT NULL,
                isActive INTEGER NOT NULL,
                createdAt TEXT NOT NULL,
                updatedAt TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS categories (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                icon TEXT NOT NULL,
                color TEXT NOT NULL,
                type TEXT NOT NULL,
                isDefault INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS transactions (
                id TEXT NOT NULL PRIMARY KEY,
                accountId TEXT NOT NULL,
                toAccountId TEXT,
                categoryId TEXT,
                type TEXT NOT NULL,
                amount TEXT NOT NULL,
                currency TEXT NOT NULL,
                merchant TEXT,
                description TEXT,
                notes TEXT,
                transactionDate TEXT NOT NULL,
                createdAt TEXT NOT NULL,
                updatedAt TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}
