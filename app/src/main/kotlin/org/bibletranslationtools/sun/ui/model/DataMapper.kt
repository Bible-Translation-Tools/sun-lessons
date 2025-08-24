package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.api.CardData
import org.bibletranslationtools.sun.api.LessonData
import org.bibletranslationtools.sun.api.SentenceData
import org.bibletranslationtools.sun.api.SymbolData
import org.bibletranslationtools.sun.data.BookDataStore
import org.bibletranslationtools.sun.data.entity.CardEntity
import org.bibletranslationtools.sun.data.entity.LessonEntity
import org.bibletranslationtools.sun.data.entity.LessonWithData
import org.bibletranslationtools.sun.data.entity.SentenceEntity
import org.bibletranslationtools.sun.data.entity.SymbolEntity
import org.bibletranslationtools.sun.utils.toLocalDateTime
import org.bibletranslationtools.sun.utils.toTimestamp

interface DataMapper {
    fun toItem(entity: LessonEntity): LessonItem
    fun toItem(data: LessonData): LessonItem
    fun toItem(data: LessonWithData): LessonItem
    fun toItem(entity: CardEntity): CardItem
    fun toItem(data: CardData): CardItem
    fun toItem(entity: SentenceEntity): SentenceItem
    fun toItem(data: SentenceData): SentenceItem
    fun toItem(entity: SymbolEntity): SymbolItem
    fun toItem(data: SymbolData): SymbolItem
    fun toEntity(item: LessonItem): LessonEntity
    fun toEntity(card: CardItem): CardEntity
    fun toEntity(item: SentenceItem): SentenceEntity
    fun toEntity(item: SymbolItem): SymbolEntity
    fun toEntity(data: LessonData): LessonEntity
    fun toEntity(data: CardData): CardEntity
    fun toEntity(data: SentenceData): SentenceEntity
    fun toEntity(data: SymbolData): SymbolEntity
}

class DataMapperImpl(
    private val bookDataStore: BookDataStore
) : DataMapper {

    override fun toItem(entity: LessonEntity): LessonItem {
        val book = entity.book?.let { bookDataStore.getBook(it) }
        return LessonItem(
            book = book,
            chapter = entity.chapter,
            verse = entity.verse,
            sort = entity.sort,
            author = entity.author,
            createdAt = entity.createdAt.toLocalDateTime(),
            updatedAt = entity.updatedAt.toLocalDateTime(),
            id = entity.id
        )
    }

    override fun toItem(data: LessonData): LessonItem {
        val book = data.book?.let { bookDataStore.getBook(it) }
        return LessonItem(
            book = book,
            chapter = data.chapter,
            verse = data.verse,
            sort = data.sort,
            author = data.author ?: "unknown",
            cards = data.cards.map(::toItem),
            sentences = data.sentences.map(::toItem),
            createdAt = data.createdAt.toLocalDateTime(),
            updatedAt = data.updatedAt.toLocalDateTime()
        )
    }

    override fun toItem(data: LessonWithData): LessonItem {
        val book = data.lesson.book?.let { bookDataStore.getBook(it) }
        return LessonItem(
            book = book,
            chapter = data.lesson.chapter,
            verse = data.lesson.verse,
            sort = data.lesson.sort,
            author = data.lesson.author,
            createdAt = data.lesson.createdAt.toLocalDateTime(),
            updatedAt = data.lesson.updatedAt.toLocalDateTime(),
            cards = data.cards.map(::toItem),
            sentences = data.sentences.map(::toItem),
            id = data.lesson.id
        )
    }

    override fun toEntity(item: LessonItem) = LessonEntity(
        book = item.book?.slug,
        chapter = item.chapter,
        verse = item.verse,
        sort = item.sort,
        author = item.author,
        createdAt = item.createdAt.toTimestamp(),
        updatedAt = item.updatedAt.toTimestamp(),
        id = item.id
    )

    override fun toEntity(card: CardItem) = CardEntity(
        symbol = card.symbol,
        sort = card.sort,
        image = card.image,
        learned = card.learned,
        tested = card.tested,
        lessonId = card.lessonId,
        id = card.id
    )

    override fun toItem(entity: CardEntity) = CardItem(
        symbol = entity.symbol,
        sort = entity.sort,
        image = entity.image,
        learned = entity.learned,
        tested = entity.tested,
        lessonId = entity.lessonId,
        passed = false,
        correct = null,
        id = entity.id
    )

    override fun toItem(data: CardData) = CardItem(
        symbol = data.symbol,
        sort = data.sort,
        image = data.image,
        learned = false,
        tested = false,
        lessonId = 0
    )

    override fun toEntity(item: SentenceItem) = SentenceEntity(
        sort = item.sort,
        image = item.image,
        learned = item.learned,
        tested = item.tested,
        lessonId = item.lessonId,
        id = item.id
    )

    override fun toItem(entity: SentenceEntity) = SentenceItem(
        sort = entity.sort,
        image = entity.image,
        learned = entity.learned,
        tested = entity.tested,
        lessonId = entity.lessonId,
        symbols = emptyList(),
        passed = false,
        id = entity.id
    )

    override fun toItem(data: SentenceData) = SentenceItem(
        sort = data.sort,
        learned = false,
        tested = false,
        lessonId = 0,
        symbols = data.symbols.map(::toItem),
        image = data.image
    )

    override fun toEntity(item: SymbolItem) = SymbolEntity(
        name = item.name,
        sort = item.sort,
        sentenceId = item.sentenceId,
        id = item.id
    )

    override fun toItem(entity: SymbolEntity) = SymbolItem(
        name = entity.name,
        sort = entity.sort,
        sentenceId = entity.sentenceId,
        selected = false,
        correct = null,
        id = entity.id
    )

    override fun toItem(data: SymbolData) = SymbolItem(
        name = data.name,
        sort = data.sort,
        sentenceId = 0
    )

    override fun toEntity(data: LessonData) = LessonEntity()

    override fun toEntity(data: CardData) = CardEntity(
        symbol = data.symbol,
        sort = data.sort,
        image = data.image,
        learned = false,
        tested = false
    )

    override fun toEntity(data: SentenceData) = SentenceEntity(
        sort = data.sort,
        image = data.image,
        learned = false,
        tested = false
    )

    override fun toEntity(data: SymbolData) = SymbolEntity(
        sort = data.sort,
        name = data.name
    )
}