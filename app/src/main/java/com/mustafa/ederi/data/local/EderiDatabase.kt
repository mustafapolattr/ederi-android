package com.mustafa.ederi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mustafa.ederi.data.local.dao.AppPreferenceDao
import com.mustafa.ederi.data.local.entity.AppPreferenceEntity

/**
 * Local Room cache (spec §11). [AppPreferenceEntity] is infrastructure-only
 * (non-financial local key/value state) needed because Room requires at
 * least one entity to compile; domain entities (Account, Transaction, ...)
 * are added per feature phase, not here. Every schema bump from here on must
 * ship an explicit [androidx.room.migration.Migration] — never
 * fallbackToDestructiveMigration in release builds — using the exported
 * schema JSON under app/schemas as the diff baseline.
 */
@Database(entities = [AppPreferenceEntity::class], version = 1, exportSchema = true)
abstract class EderiDatabase : RoomDatabase() {
    abstract fun appPreferenceDao(): AppPreferenceDao
}
