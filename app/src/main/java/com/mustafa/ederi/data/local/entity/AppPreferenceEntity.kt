package com.mustafa.ederi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Generic local key/value store for non-financial app state (e.g. last sync
 * timestamp, onboarding flags). Room requires at least one entity to compile
 * a @Database, and this table is the infrastructure placeholder for that —
 * no financial/domain entities are added until their owning feature phase
 * is approved.
 */
@Entity(tableName = "app_preferences")
data class AppPreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)
