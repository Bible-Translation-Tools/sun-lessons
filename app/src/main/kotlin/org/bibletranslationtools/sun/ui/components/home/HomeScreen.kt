package org.bibletranslationtools.sun.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.LearnCard
import org.bibletranslationtools.sun.ui.control.TopAppBar

@Composable
fun HomeScreen(component: HomeComponent, parentPadding: PaddingValues) {

    Scaffold(
        topBar = {
            TopAppBar {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.home),
                        contentDescription = "home"
                    )
                    Text(
                        text = stringResource(R.string.home),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        modifier = Modifier.padding(parentPadding),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                LearnCard(onLearnClicked = component::onLearnClicked)
            }
        }
    }
}