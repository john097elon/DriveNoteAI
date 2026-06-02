package com.drivenote.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drivenote.app.data.db.entity.SettingEntity

@Dao
interface SettingsDao {
    @Query("SELECT value FROM settings WHERE key = :key")
    suspend fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: SettingEntity)
}
