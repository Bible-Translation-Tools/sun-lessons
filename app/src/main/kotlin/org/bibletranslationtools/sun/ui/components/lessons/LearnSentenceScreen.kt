package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TallyText
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.learn.PagerIndicator
import org.bibletranslationtools.sun.ui.control.learn.PagerNavButton
import org.bibletranslationtools.sun.ui.control.learn.SentencePage
import org.bibletranslationtools.sun.ui.model.LessonMode

@Composable
fun LearnSentenceScreen(component: LearnSentenceComponent, parentPadding: PaddingValues) {

    val model by component.model.subscribeAsState()

    val pagerState = rememberPagerState(pageCount = { model.sentences.size })
    val coroutineScope = rememberCoroutineScope()

    var nextEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        if (model.sentences.isNotEmpty()) {
            model.sentences.let { sentences ->
                val sentence = sentences[pagerState.currentPage]
                val done = if (model.mode == LessonMode.REPEAT) {
                    sentence.passed
                } else sentence.learned
                nextEnabled = done
            }
            if (pagerState.currentPage > 0) {
                component.saveLastPosition(pagerState.currentPage)
            }
        }
    }

    LaunchedEffect(model.lastPosition) {
        if (model.mode == LessonMode.NORMAL && model.lastPosition > 0) {
            pagerState.scrollToPage(model.lastPosition)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(onBackClick = component::onBackClick) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.lesson_name, model.lessonId),
                        fontWeight = FontWeight.Bold
                    )
                    TallyText(model.lessonId)
                }
            }
        },
        modifier = Modifier.padding(parentPadding),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 20.dp)
                        .graphicsLayer { clip = false },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        PagerNavButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "previous"
                        ) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(50.dp))
                    }

                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = nextEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(400.dp)
                    ) { pageIndex ->
                        SentencePage(
                            sentence = model.sentences[pageIndex],
                            onFrontFlipped = {
                                component.onCardFlipped(it)
                                nextEnabled = true
                            }
                        )
                    }

                    PagerNavButton(
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "next",
                        enabled = nextEnabled
                    ) {
                        val unlearnedItem = model.sentences.indexOfFirst {
                            if (model.mode == LessonMode.REPEAT) !it.passed else !it.learned
                        }

                        when {
                            pagerState.currentPage < model.sentences.size - 1 -> pagerState.currentPage + 1
                            unlearnedItem > -1 && unlearnedItem < model.sentences.size - 1 -> unlearnedItem
                            else -> null
                        }?.let { next ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(next)
                            }
                        } ?: run {
                            component.finishLesson()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                PagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}