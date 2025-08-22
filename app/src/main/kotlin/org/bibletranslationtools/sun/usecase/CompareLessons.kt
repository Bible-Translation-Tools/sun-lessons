package org.bibletranslationtools.sun.usecase

import org.bibletranslationtools.sun.ui.model.DownloadStatus
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.LessonSuite

private data class LessonId(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val sort: Int,
    val author: String
)

private data class LessonPair(
    val local: LessonSuite?,
    val remote: LessonSuite?
)

private fun LessonItem.toLessonId() = LessonId(
    book = book ?: throw IllegalArgumentException("book is null"),
    chapter = chapter ?: throw IllegalArgumentException("chapter is null"),
    verse = verse ?: throw IllegalArgumentException("verse is null"),
    sort = sort,
    author = author
)

class CompareLessons {

    fun compare(
        localLessons: List<LessonSuite>,
        remoteLessons: List<LessonSuite>
    ): List<LessonSuite> {

        val lessonsMap = mutableMapOf<LessonId, LessonPair>()

        localLessons.forEach {
            lessonsMap[it.lesson.toLessonId()] = LessonPair(it, null)
        }

        remoteLessons.forEach {
            val id = it.lesson.toLessonId()
            val pair = lessonsMap[id]
            if (pair != null) {
                lessonsMap[id] = pair.copy(remote = it)
            } else {
                lessonsMap[id] = LessonPair(null, it)
            }
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

    private fun setDownloadStatus(suite: LessonSuite): LessonSuite {
        return setStatus(suite, DownloadStatus.DOWNLOAD)
    }

    private fun setUpdateStatus(local: LessonSuite, remote: LessonSuite): LessonSuite {
        if (local.lesson.updatedAt == remote.lesson.updatedAt) {
            return setDoneStatus(remote)
        }

        return setStatus(remote, DownloadStatus.UPDATE)
    }

    private fun setDoneStatus(suite: LessonSuite): LessonSuite {
        return setStatus(suite, DownloadStatus.DONE)
    }

    private fun setStatus(suite: LessonSuite, status: DownloadStatus): LessonSuite {
        return suite.copy(downloadStatus = status)
    }
}