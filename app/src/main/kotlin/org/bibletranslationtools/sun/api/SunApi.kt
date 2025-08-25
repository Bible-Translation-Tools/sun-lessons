package org.bibletranslationtools.sun.api

import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Utils

data class LessonRequest(
    val book: String? = null,
    val chapter: Int? = null,
    val verse: Int? = null,
    val author: String? = null
)

interface SunApi {
    suspend fun getLessonCatalog(request: LessonRequest): LessonCatalog
}

class SunApiImpl(
    private val httpClient: HttpClient,
    private val assetReader: AssetReader
) : SunApi {

    override suspend fun getLessonCatalog(request: LessonRequest): LessonCatalog {
        val (catalog, lessons) = withContext(Dispatchers.IO) {
            val json = assetReader.readText("test_catalog.json")
            val catalog: LessonCatalog = Utils.JsonLenient.decodeFromString(json)
            catalog to catalog.lessons.asSequence()
                .filter { request.book == null || it.book == request.book }
                .filter { request.chapter == null || it.chapter == request.chapter }
                .filter { request.verse == null || it.verse == request.verse }
                .filter { request.author == null || it.author == request.author }
                .toList()
        }

        return catalog.copy(lessons = lessons)
    }
}