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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconComponent(
    name: String,
    painter: Painter?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeight = remember { mutableIntStateOf(100) }
    val density = LocalDensity.current
    val itemWidth: Dp = with(density) {
        itemHeight.intValue.toDp()
    }
    Box(
        modifier = modifier.width(itemWidth)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 8.dp,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(25)
                )
                .onSizeChanged { size ->
                    itemHeight.intValue = size.height
                },
            shape = RoundedCornerShape(25),
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