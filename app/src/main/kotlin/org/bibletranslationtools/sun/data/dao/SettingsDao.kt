package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.SettingEntity

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: SettingEntity)

    @Delete
    suspend fun delete(setting: SettingEntity)

    @Update
    suspend fun update(setting: SettingEntity)

    @Query("SELECT * FROM settings WHERE name = :name")
    suspend fun get(name: String): SettingEntity?
}