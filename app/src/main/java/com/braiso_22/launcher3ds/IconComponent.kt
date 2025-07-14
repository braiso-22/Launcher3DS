package com.braiso_22.launcher3ds

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun IconComponent(
    name: String,
    painter: BitmapPainter?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val borderColor by remember(selected) {
        derivedStateOf {
            if (selected) {
                colorScheme.primary
            } else {
                colorScheme.primaryContainer
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .aspectRatio(1f)
                .border(
                    width = 8.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(25)
                ),
            shape = RoundedCornerShape(25),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainer
            ),
            onClick = onClick
        ) {
            if (painter != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painter,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(1 / 2f)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun IconComponentPreview() {
    MaterialTheme {
        Scaffold {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                IconComponent(
                    name = "Tia Jensen",
                    painter = null,
                    selected = false,
                    onClick = {}
                )
            }
        }
    }
}