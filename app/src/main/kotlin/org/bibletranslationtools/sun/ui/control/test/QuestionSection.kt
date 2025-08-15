package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.bibletranslationtools.sun.R

@Composable
fun QuestionSection(
    imageUri: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = stringResource(R.string.test_symbols_hint),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit,
            error = painterResource(id = R.drawable.ic_write)
        )
        Text(
            text = stringResource(id = R.string.test_symbols_hint),
            modifier = Modifier.padding(top = 10.dp),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}