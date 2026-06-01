package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.SymbolDao
import org.bibletranslationtools.sun.data.entity.SymbolEntity

interface SymbolRepository {
    suspend fun insert(symbol: SymbolEntity)
    suspend fun insertAll(symbols: List<SymbolEntity>)
    suspend fun delete(symbol: SymbolEntity)
    suspend fun deleteAll(symbols: List<SymbolEntity>)
    suspend fun update(symbol: SymbolEntity)
    suspend fun updateAll(symbols: List<SymbolEntity>)
    suspend fun getBySentence(sentenceId: Long): List<SymbolEntity>
    suspend fun updatePrefill(id: Long, prefill: Boolean)
}

class SymbolRepositoryImpl(
    private val symbolDao: SymbolDao
) : SymbolRepository {
    override suspend fun insert(symbol: SymbolEntity) {
        symbolDao.insert(symbol)
    }

    override suspend fun insertAll(symbols: List<SymbolEntity>) {
        symbolDao.insertAll(symbols)
    }

    override suspend fun delete(symbol: SymbolEntity) {
        symbolDao.delete(symbol)
    }

    override suspend fun deleteAll(symbols: List<SymbolEntity>) {
        symbolDao.deleteAll(symbols)
    }

    override suspend fun update(symbol: SymbolEntity) {
        symbolDao.update(symbol)
    }

    override suspend fun updateAll(symbols: List<SymbolEntity>) {
        symbolDao.updateAll(symbols)
    }

    override suspend fun getBySentence(sentenceId: Long): List<SymbolEntity> {
        return symbolDao.getBySentence(sentenceId)
    }

    override suspend fun updatePrefill(id: Long, prefill: Boolean) {
        symbolDao.updatePrefill(id, prefill)
    }
}