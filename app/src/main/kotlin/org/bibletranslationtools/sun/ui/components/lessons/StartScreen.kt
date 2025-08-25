package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.ui.control.NextButton
import org.bibletranslationtools.sun.ui.control.TallyText
import org.bibletranslationtools.sun.ui.control.TopAppBar

@Composable
fun StartScreen(component: StartComponent) {
    val model by component.model.subscribeAsState()

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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    Text(
                        text = stringResource(id = model.sectionTitle),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = model.lesson?.name ?: "error",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Image(
                        painter = painterResource(id = model.imageResource),
                        contentDescription = stringResource(model.sectionTitle),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .width(240.dp)
                            .padding(top = 80.dp)
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