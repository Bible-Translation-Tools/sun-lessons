package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.ui.control.TallyText
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.test.ActionButtons
import org.bibletranslationtools.sun.ui.control.test.AnswerArea
import org.bibletranslationtools.sun.ui.control.test.QuestionSection

@Composable
fun TestSentenceScreen(component: TestSentenceComponent) {

    val model by component.model.subscribeAsState()

    var showCorrectAnswer by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(onBackClick = component::onBackClick) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = model.lesson?.name ?: "error",
                    fontWeight = FontWeight.Bold
                )
                TallyText(model.lesson?.sort ?: 0)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuestionSection(
                    imageUri = model.imageUri,
                    modifier = Modifier.height(400.dp)
                )

                AnswerArea(
                    state = model,
                    showCorrectAnswer = showCorrectAnswer,
                    onSymbolSelected = component::onSymbolSelected,
                    modifier = Modifier.weight(1f)
                )

                ActionButtons(
                    isCorrect = model.isCorrect,
                    questionDone = model.questionDone,
                    onNextSentence = component::setNextSentence,
                    showCorrectAnswer = showCorrectAnswer,
                    onShowCorrectAnswer = { show ->
                        showCorrectAnswer = show ?: !showCorrectAnswer
                    }
                )
            }
        }
    }
}