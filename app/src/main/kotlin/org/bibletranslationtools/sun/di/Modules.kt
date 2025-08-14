package org.bibletranslationtools.sun.di

import androidx.room.Room
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.CardRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepositoryImpl
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.AssetReaderImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "sun.db"
        ).build()
    }
    single { get<AppDatabase>().getLessonDao() }
    single { get<AppDatabase>().getCardDao() }
    single { get<AppDatabase>().getSymbolDao() }
    single { get<AppDatabase>().getSentenceDao() }
    single { get<AppDatabase>().getSettingDao() }

    singleOf(::LessonRepositoryImpl).bind<LessonRepository>()
    singleOf(::CardRepositoryImpl).bind<CardRepository>()
    singleOf(::SentenceRepositoryImpl).bind<SentenceRepository>()
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::AssetReaderImpl).bind<AssetReader>()
}