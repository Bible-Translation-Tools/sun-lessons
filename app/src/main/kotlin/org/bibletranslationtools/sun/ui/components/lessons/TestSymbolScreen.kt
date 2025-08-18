package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TallyText
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.test.AnswerGrid
import org.bibletranslationtools.sun.ui.control.test.NextButtonSection
import org.bibletranslationtools.sun.ui.control.test.QuestionSection

@Composable
fun TestSymbolScreen(component: TestSymbolComponent, parentPadding: PaddingValues) {
    val model by component.model.subscribeAsState()

    val imageUri = "file:///android_asset/images/symbols/" +
            (model.currentCard?.image ?: "0.jpg")

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
                    .padding(horizontal = 40.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuestionSection(
                    imageUri = imageUri,
                    modifier = Modifier.weight(1f)
                )

                AnswerGrid(
                    choices = model.choices,
                    answer = model.answer,
                    onCardSelected = { selectedCard ->
                        component.checkAnswer(selectedCard)
                    },
                    showStatusIcon = model.questionDone,
                    modifier = Modifier.weight(1f)
                )

                NextButtonSection(
                    isVisible = model.questionDone,
                    onNextClicked = {
                        component.setNextQuestion()
                    }
                )
            }
        }
    }
}