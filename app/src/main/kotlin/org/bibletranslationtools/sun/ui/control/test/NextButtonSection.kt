package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.R

@Composable
fun NextButtonSection(
    isVisible: Boolean,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Button(
            onClick = onNextClicked,
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(text = stringResource(id = R.string.next))
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "next"
            )
        }
    } else {
        Spacer(modifier = Modifier.height(40.dp))
    }
}