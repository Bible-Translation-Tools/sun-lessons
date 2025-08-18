package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.NextButton
import org.bibletranslationtools.sun.ui.control.TallyText
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.utils.Section

@Composable
fun CompleteScreen(component: CompleteComponent, parentPadding: PaddingValues) {
    val model by component.model.subscribeAsState()

    val image = if (model.section == Section.TEST_SENTENCES) {
        R.drawable.finish
    } else {
        R.drawable.track_progress
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    Image(
                        painter = painterResource(image),
                        contentDescription = "completed",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.width(120.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
                    )

                    Text(
                        text = stringResource(model.sectionTitle, model.lessonId),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                NextButton(
                    modifier = Modifier
                        .padding(horizontal = 60.dp)
                        .padding(bottom = 50.dp),
                    onClick = component::onNextClicked
                )
            }
        }
    }
}