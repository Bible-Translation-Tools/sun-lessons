package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.SettingsDao
import org.bibletranslationtools.sun.data.model.Setting

interface SettingsRepository {
    suspend fun insert(setting: Setting)
    suspend fun delete(setting: Setting)
    suspend fun update(setting: Setting)
    suspend fun insertOrUpdate(setting: Setting)
    suspend fun get(name: String): Setting?
}

class SettingsRepositoryImpl(private val settingDao: SettingsDao) : SettingsRepository {
    override suspend fun insert(setting: Setting) {
        settingDao.insert(setting)
    }

    override suspend fun delete(setting: Setting) {
        settingDao.delete(setting)
    }

    override suspend fun update(setting: Setting) {
        settingDao.update(setting)
    }

    override suspend fun insertOrUpdate(setting: Setting) {
        settingDao.get(setting.name)?.let { update(setting) } ?: insert(setting)
    }

    override suspend fun get(name: String): Setting? {
        return settingDao.get(name)
    }
}