package org.bibletranslationtools.sun.di

import android.content.Context
import coil3.imageLoader
import coil3.request.ImageRequest
import org.bibletranslationtools.sun.api.SunApi
import org.bibletranslationtools.sun.api.SunApiImpl
import org.bibletranslationtools.sun.api.createHttpClient
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.CardRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.SymbolRepository
import org.bibletranslationtools.sun.data.repositories.SymbolRepositoryImpl
import org.bibletranslationtools.sun.usecase.GetLessonsWithDownloadStatus
import org.bibletranslationtools.sun.usecase.DownloadLesson
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.AssetReaderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    single {
        AppDatabase.getDatabase(get())
    }
    single { get<AppDatabase>().getLessonDao() }
    single { get<AppDatabase>().getCardDao() }
    single { get<AppDatabase>().getSymbolDao() }
    single { get<AppDatabase>().getSentenceDao() }
    single { get<AppDatabase>().getSettingDao() }

    singleOf(::createHttpClient)
    singleOf(::SunApiImpl).bind<SunApi>()

    factory { get<Context>().imageLoader }
    factory { ImageRequest.Builder(get<Context>()) }

    factoryOf(::GetLessonsWithDownloadStatus)
    factoryOf(::DownloadLesson)

    singleOf(::LessonRepositoryImpl).bind<LessonRepository>()
    singleOf(::CardRepositoryImpl).bind<CardRepository>()
    singleOf(::SentenceRepositoryImpl).bind<SentenceRepository>()
    singleOf(::SymbolRepositoryImpl).bind<SymbolRepository>()
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::AssetReaderImpl).bind<AssetReader>()
}