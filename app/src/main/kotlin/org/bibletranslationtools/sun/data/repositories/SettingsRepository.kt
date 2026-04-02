package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.SettingsDao
import org.bibletranslationtools.sun.data.entity.SettingEntity

interface SettingsRepository {
    suspend fun insert(setting: SettingEntity)
    suspend fun delete(setting: SettingEntity)
    suspend fun update(setting: SettingEntity)
    suspend fun insertOrUpdate(setting: SettingEntity)
    suspend fun get(name: String): SettingEntity?
}

class SettingsRepositoryImpl(private val settingDao: SettingsDao) : SettingsRepository {
    override suspend fun insert(setting: SettingEntity) {
        settingDao.insert(setting)
    }

    override suspend fun delete(setting: SettingEntity) {
        settingDao.delete(setting)
    }

    override suspend fun update(setting: SettingEntity) {
        settingDao.update(setting)
    }

    override suspend fun insertOrUpdate(setting: SettingEntity) {
        settingDao.get(setting.name)?.let { update(setting) } ?: insert(setting)
    }

    override suspend fun get(name: String): SettingEntity? {
        return settingDao.get(name)
    }
}