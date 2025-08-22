package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.SymbolEntity

@Dao
interface SymbolDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(symbol: SymbolEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(symbols: List<SymbolEntity>)

    @Delete
    suspend fun delete(symbol: SymbolEntity)

    @Delete
    suspend fun deleteAll(symbols: List<SymbolEntity>)

    @Update
    suspend fun update(symbol: SymbolEntity)

    @Update
    suspend fun updateAll(symbols: List<SymbolEntity>)
}