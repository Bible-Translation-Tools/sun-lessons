package org.bibletranslationtools.sun.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TopAppBar

@Composable
fun SettingsScreen(component: SettingsComponent, parentPadding: PaddingValues) {
    Scaffold(
        topBar = {
            TopAppBar(onBackClick = {}) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "Lessons"
                )
                Text(
                    text = stringResource(R.string.settings),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        modifier = Modifier.padding(parentPadding),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Settings")
        }
    }
}