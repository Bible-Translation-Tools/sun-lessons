package org.bibletranslationtools.sun.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.bibletranslationtools.sun.data.dao.CardDao
import org.bibletranslationtools.sun.data.dao.LessonDao
import org.bibletranslationtools.sun.data.dao.SentenceDao
import org.bibletranslationtools.sun.data.dao.SettingsDao
import org.bibletranslationtools.sun.data.dao.SymbolDao
import org.bibletranslationtools.sun.data.entity.CardEntity
import org.bibletranslationtools.sun.data.entity.LessonEntity
import org.bibletranslationtools.sun.data.entity.SentenceEntity
import org.bibletranslationtools.sun.data.entity.SettingEntity
import org.bibletranslationtools.sun.data.entity.SymbolEntity
import kotlin.concurrent.Volatile

@Database(
    entities = [
        CardEntity::class,
        LessonEntity::class,
        SettingEntity::class,
        SentenceEntity::class,
        SymbolEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCardDao(): CardDao
    abstract fun getLessonDao(): LessonDao
    abstract fun getSettingDao(): SettingsDao
    abstract fun getSentenceDao(): SentenceDao
    abstract fun getSymbolDao(): SymbolDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, AppDatabase::class.java, "sun.db")
//                    .setQueryCallback(
//                        object : QueryCallback {
//                            override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
//                                println("RoomQuery - Query: $sqlQuery, Args: $bindArgs")
//                            }
//                        },
//                        Executors.newSingleThreadExecutor()
//                    )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE!!
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("PRAGMA foreign_keys=OFF")
                db.execSQL("""
                    CREATE TABLE sentences_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        sort INTEGER NOT NULL, 
                        image TEXT, 
                        learned INTEGER NOT NULL, 
                        tested INTEGER NOT NULL, 
                        lessonId INTEGER NOT NULL,
                        FOREIGN KEY(lessonId) REFERENCES lessons(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO sentences_new (id, sort, image, learned, tested, lessonId)
                    SELECT id, sort, image, learned, tested, lessonId FROM sentences
                """.trimIndent())

                db.execSQL("DROP TABLE sentences")
                db.execSQL("ALTER TABLE sentences_new RENAME TO sentences")
                db.execSQL("PRAGMA foreign_keys=ON")
            }
        }
    }
}