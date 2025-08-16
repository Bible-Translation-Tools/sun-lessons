package org.bibletranslationtools.sun.ui.components.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.progress.LessonBox
import org.bibletranslationtools.sun.ui.control.progress.LessonsLearnedHeader
import org.bibletranslationtools.sun.ui.control.progress.ProgressSection
import org.bibletranslationtools.sun.ui.control.progress.ScreenHeader

@Composable
fun ProgressScreen(component: ProgressComponent) {
    val model by component.model.subscribeAsState()

    LaunchedEffect(Unit) {
        component.setTopAppBar {
            TopAppBar(onBackClick = component::onBackClick) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(R.drawable.sun_logo),
                        contentDescription = "logo"
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            ScreenHeader()

            Spacer(modifier = Modifier.height(12.dp))

            ProgressSection(model.lessons)

            Spacer(modifier = Modifier.height(12.dp))

            LessonsLearnedHeader()

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(model.lessons.size) { index ->
                    val lesson = model.lessons[index]
                    LessonBox(
                        lesson = lesson,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}