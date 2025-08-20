package org.bibletranslationtools.sun.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.settings.CustomTextButton

@Composable
fun SettingsListScreen(component: SettingsListComponent) {

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(onBackClick = component::onBackClick) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Lessons"
            )
            Text(
                text = stringResource(R.string.settings),
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                CustomTextButton(
                    onClick = component::onDownloadsClick,
                    icon = Icons.Default.SystemUpdateAlt,
                    text = stringResource(R.string.downloads)
                )

                CustomTextButton(
                    onClick = component::onCheckUpdatesClick,
                    icon = Icons.Default.RestartAlt,
                    text = stringResource(R.string.check_updates)
                )
            }
        }
    }
}