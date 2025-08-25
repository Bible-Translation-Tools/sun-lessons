package org.bibletranslationtools.sun.api

import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.ui.model.GroupId
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Utils

interface SunApi {
    suspend fun getLessonCatalog(groupId: GroupId): LessonCatalog
}

class SunApiImpl(
    private val httpClient: HttpClient,
    private val assetReader: AssetReader
) : SunApi {

    override suspend fun getLessonCatalog(groupId: GroupId): LessonCatalog {
        val (catalog, lessons) = withContext(Dispatchers.IO) {
            val json = assetReader.readText("test_catalog.json")
            val catalog: LessonCatalog = Utils.JsonLenient.decodeFromString(json)
            catalog to catalog.lessons.asSequence()
                .filter { groupId.book == null || it.book == groupId.book }
                .filter { groupId.chapter == null || it.chapter == groupId.chapter }
                .filter { groupId.verse == null || it.verse == groupId.verse }
                .filter { groupId.author == null || it.author == groupId.author }
                .toList()
        }

        return catalog.copy(lessons = lessons)
    }
}