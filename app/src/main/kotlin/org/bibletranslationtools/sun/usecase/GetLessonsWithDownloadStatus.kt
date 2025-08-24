package org.bibletranslationtools.sun.usecase

import org.bibletranslationtools.sun.api.LessonRequest
import org.bibletranslationtools.sun.api.SunApi
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.ui.model.DownloadStatus
import org.bibletranslationtools.sun.ui.model.GroupId
import org.bibletranslationtools.sun.ui.model.LessonGroup
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.DataMapper

private data class LessonPair(
    val local: LessonGroup?,
    val remote: LessonGroup?
)

class GetLessonsWithDownloadStatus(
    private val sunApi: SunApi,
    private val dataMapper: DataMapper,
    private val lessonRepository: LessonRepository,
) {

    suspend operator fun invoke(book: String, chapter: Int): List<LessonItem> {

        val remoteLessons = sunApi.getLessonCatalog(
            LessonRequest(book = book, chapter = chapter)
        )
            .lessons.map(dataMapper::toItem)

        val localLessons = lessonRepository.getAll(book, chapter)
            .map(dataMapper::toItem)

        val localGroups = localLessons
            .groupBy { it.groupId }
            .mapValues { (groupId, items) -> LessonGroup(groupId, items) }

        val remoteGroups = remoteLessons
            .groupBy { it.groupId }
            .mapValues { (groupId, items) -> LessonGroup(groupId, items) }

        val allGroupIds = localGroups.keys + remoteGroups.keys

        val lessonsMap: Map<GroupId, LessonPair> = allGroupIds.associateWith { key ->
            LessonPair(
                local = localGroups[key],
                remote = remoteGroups[key]
            )
        }

        return lessonsMap.mapNotNull { (_, pair) ->
            val local = pair.local
            val remote = pair.remote

            when {
                local == null && remote != null -> setDownloadStatus(remote)
                local != null && remote != null -> setUpdateStatus(local, remote)
                else -> local?.let { setDoneStatus(it) }
            }
        }
    }

    private fun setDownloadStatus(group: LessonGroup): LessonItem {
        return setStatus(group.lessons.first(), DownloadStatus.DOWNLOAD)
    }

    private fun setUpdateStatus(local: LessonGroup, remote: LessonGroup): LessonItem {
        val hasUpdates = hasGroupDifferences(local, remote)

        return if (hasUpdates) {
            setStatus(remote.lessons.first(), DownloadStatus.UPDATE)
        } else {
            setDoneStatus(remote)
        }
    }

    private fun setDoneStatus(group: LessonGroup): LessonItem {
        return setStatus(group.lessons.first(), DownloadStatus.DONE)
    }

    private fun setStatus(lesson: LessonItem, status: DownloadStatus): LessonItem {
        return lesson.copy(downloadStatus = status)
    }

    private fun hasGroupDifferences(local: LessonGroup, remote: LessonGroup): Boolean {
        val localLessons = local.lessons
        val remoteLessons = remote.lessons

        if (localLessons.size != remoteLessons.size) return true

        val localLessonsById = localLessons.associateBy { it.uniqueId }
        val remoteLessonsById = remoteLessons.associateBy { it.uniqueId }

        if (localLessonsById.keys != remoteLessonsById.keys) return true

        return localLessonsById.keys.any { id ->
            val localItem = localLessonsById[id]!!
            val remoteItem = remoteLessonsById[id]!!
            localItem.updatedAt != remoteItem.updatedAt
        }
    }
}