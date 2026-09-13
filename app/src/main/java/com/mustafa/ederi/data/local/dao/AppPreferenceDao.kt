package com.mustafa.ederi.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafa.ederi.data.local.entity.AppPreferenceEntity

@Dao
interface AppPreferenceDao {
    @Query("SELECT * FROM app_preferences WHERE `key` = :key")
    suspend fun get(key: String): AppPreferenceEntity?

    @Upsert
    suspend fun set(preference: AppPreferenceEntity)
}
