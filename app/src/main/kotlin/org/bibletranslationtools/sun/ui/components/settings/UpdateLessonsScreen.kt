package org.bibletranslationtools.sun.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TopAppBar

@Composable
fun UpdateLessonsScreen(component: UpdateLessonsComponent) {

    val model by component.model.subscribeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(onBackClick = component::onBackClick) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.RestartAlt,
                contentDescription = "updates"
            )
            Text(
                text = stringResource(R.string.updates),
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(model.lessons) { index, lesson ->
//                        DownloadCard(
//                            suite = lesson,
//                            progress = Random.nextFloat(),
//                            onClick = {
//                                component.onLessonSelected(lesson)
//                            },
//                            modifier = Modifier.fillMaxWidth()
//                        )
                    }
                }
            }
        }
    }
}