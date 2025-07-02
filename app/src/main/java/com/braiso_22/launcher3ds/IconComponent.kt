package com.braiso_22.launcher3ds

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconComponent(
    name: String,
    painter: Painter?,
    selected: Boolean,
    dpSize: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(dpSize)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = dpSize / 8,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(dpSize / 6)
                ),
            shape = RoundedCornerShape(dpSize / 6),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
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
                        modifier = Modifier
                            .size(dpSize / 2)
                            .fillMaxSize()
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
                    dpSize = 150.dp,
                    onClick = {}
                )
            }
        }
    }
}