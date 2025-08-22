package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.BookItem
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.model.DownloadStatus
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.LessonSuite
import org.bibletranslationtools.sun.ui.model.SentenceItem
import org.bibletranslationtools.sun.ui.model.SymbolItem
import org.bibletranslationtools.sun.ui.model.emptyBookItem
import org.bibletranslationtools.sun.usecase.CompareLessons
import org.bibletranslationtools.sun.usecase.UpdateLesson
import org.bibletranslationtools.sun.utils.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface DownloadLessonsComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val bookItem: BookItem = emptyBookItem(),
        val chapter: Int = 1,
        val lessons: List<LessonSuite> = emptyList(),
        val selectedLesson: LessonSuite? = null
    )

    fun onLessonSelected(lesson: LessonSuite)
    fun dismissSelectedLesson()
    fun onDownloadLessonClick()
}

class DefaultDownloadLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val bookItem: BookItem,
    private val chapter: Int
) : DownloadLessonsComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val compareLessons: CompareLessons by inject()
    private val updateLesson: UpdateLesson by inject()

    private val _model = MutableValue(DownloadLessonsComponent.Model())
    override val model: Value<DownloadLessonsComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        componentScope.launch {
            _model.update {
                it.copy(
                    bookItem = bookItem,
                    chapter = chapter
                )
            }

            loadLessons()
        }
    }

    override fun onLessonSelected(lesson: LessonSuite) {
        _model.update { it.copy(selectedLesson = lesson) }
    }

    override fun dismissSelectedLesson() {
        _model.update { it.copy(selectedLesson = null) }
    }

    override fun onDownloadLessonClick() {
        componentScope.launch {
            model.value.selectedLesson?.let { suite ->
                dismissSelectedLesson()

                //updateLesson.update(suite)

                for (i in 0..100) {
                    onDownloadProgress(suite, i / 100f)
                    delay(2000 / 100)
                }

                _model.update { state ->
                    state.copy(lessons = state.lessons.map {
                        if (it.lesson.fingerprint == suite.lesson.fingerprint) {
                            it.copy(
                                downloadStatus = DownloadStatus.DONE,
                                downloadProgress = -1f
                            )
                        } else {
                            it
                        }
                    })
                }
            }
        }
    }

    private suspend fun loadLessons() {
        val lessons = withContext(Dispatchers.Default) {
            val remoteLessons = getRemoteLessons()
            val localLessons = getLocalLessons()

            compareLessons.compare(localLessons, remoteLessons).sortedBy {
                it.lesson.name
            }
        }

        _model.update { it.copy(lessons = lessons) }
    }

    private fun onDownloadProgress(suite: LessonSuite, newProgress: Float) {
        _model.update { state ->
            state.copy(
                lessons = state.lessons.map { lesson ->
                    if (lesson.fingerprint == suite.fingerprint) {
                        lesson.copy(downloadProgress = newProgress)
                    } else {
                        lesson
                    }
                }
            )
        }
    }

    private fun getRemoteLessons(): List<LessonSuite> {
        return listOf(
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    1,
                    1,
                    "username1",
                    "2025-08-14T12:32:01".toLocalDateTime(),
                    "2025-08-18T12:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE291",
                        sort = 1,
                        image = "image1",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE15F",
                        sort = 2,
                        image = "image2",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image21",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE028",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE096",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image22",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE020",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE086",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            ),
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    2,
                    1,
                    "username2",
                    "2025-08-02T11:11:56".toLocalDateTime(),
                    "2025-08-10T01:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE125",
                        sort = 1,
                        image = "image4",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE203",
                        sort = 2,
                        image = "image5",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image23",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE11C",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE03F",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image24",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE086",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE35B",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            ),
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    1,
                    2,
                    "username1",
                    "2025-08-02T11:11:56".toLocalDateTime(),
                    "2025-08-10T01:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE112",
                        sort = 1,
                        image = "image6",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE21B",
                        sort = 2,
                        image = "image7",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image25",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE212",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE037",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image26",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE318",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE171",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            ),
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    3,
                    1,
                    "username1",
                    "2025-08-12T15:44:00".toLocalDateTime(),
                    "2025-08-16T09:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE26E",
                        sort = 1,
                        image = "image8",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE125",
                        sort = 2,
                        image = "image9",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image27",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE314",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE167",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image28",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE24C",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE356",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            ),
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    5,
                    1,
                    "max",
                    "2025-08-02T11:11:56".toLocalDateTime(),
                    "2025-08-10T01:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE111",
                        sort = 1,
                        image = "image10",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE358",
                        sort = 2,
                        image = "image11",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image29",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE322",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE17C",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image30",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE223",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE238",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            ),
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    6,
                    1,
                    "max",
                    "2025-08-07T11:11:56".toLocalDateTime(),
                    "2025-08-13T01:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE058",
                        sort = 1,
                        image = "image12",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE306",
                        sort = 2,
                        image = "image13",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image31",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE205",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE066",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image32",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE004",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE31A",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            )
        )
    }

    private fun getLocalLessons(): List<LessonSuite> {
        return listOf(
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    1,
                    1,
                    "username1",
                    "2025-08-14T12:32:01".toLocalDateTime(),
                    "2025-08-16T12:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE352",
                        sort = 2,
                        image = "image52",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE225",
                        sort = 3,
                        image = "image3",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image21",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE28E",
                                sort = 2,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE256",
                                sort = 3,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image22",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE020",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE086",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            ),
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    3,
                    1,
                    "username1",
                    "2025-08-12T15:44:00".toLocalDateTime(),
                    "2025-08-16T09:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE26E",
                        sort = 1,
                        image = "image8",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    ),
                    CardItem(
                        symbol = "\uE125",
                        sort = 2,
                        image = "image9",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image27",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE314",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE167",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    ),
                    SentenceItem(
                        sort = 2,
                        image = "image28",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE24C",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE356",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            ),
            LessonSuite(
                lesson = LessonItem(
                    bookItem.slug,
                    chapter,
                    6,
                    1,
                    "max",
                    "2025-08-07T11:11:56".toLocalDateTime(),
                    "2025-08-12T01:00:01".toLocalDateTime()
                ),
                cards = listOf(
                    CardItem(
                        symbol = "\uE058",
                        sort = 1,
                        image = "image12",
                        learned = false,
                        tested = false,
                        lessonId = 0
                    )
                ),
                sentences = listOf(
                    SentenceItem(
                        sort = 1,
                        image = "image31",
                        learned = false,
                        tested = false,
                        lessonId = 0,
                        symbols = listOf(
                            SymbolItem(
                                name = "\uE205",
                                sort = 1,
                                sentenceId = 0
                            ),
                            SymbolItem(
                                name = "\uE066",
                                sort = 2,
                                sentenceId = 0
                            )
                        )
                    )
                ),
                isAvailable = false,
                isSelected = false
            )
        )
    }
}