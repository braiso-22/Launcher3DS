package com.braiso_22.launcher3ds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

@Composable
fun IconsGridComp(
    selectedApp: Icon?,
    apps: List<Icon>,
    rows: Int,
    onClickItem: (Icon) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 8.dp,
    horizontalArrangement: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
) {
    LazyHorizontalGrid(
        modifier = modifier,
        contentPadding = PaddingValues(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(horizontalArrangement),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        rows = GridCells.Fixed(rows),
    ) {
        items(apps) { app ->
            val painter = remember(app.icon) {
                app.icon?.toBitmap()?.asImageBitmap()?.let {
                    BitmapPainter(it)
                }
            }
            IconComponent(
                name = app.name,
                painter = painter,
                selected = selectedApp == app,
                onClick = {
                    onClickItem(app)
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun IconsGridCompPreview() {
    MaterialTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                IconsGridComp(
                    selectedApp = Icon.App(
                        name = "Lavern Cabrera1", packageName = "Tamara Trevino", icon = null

                    ),
                    apps = List(10) {
                        Icon.App(
                            name = "Lavern Cabrera${it}",
                            packageName = "Tamara Trevino",
                            icon = null

                        )
                    },
                    rows = 3,
                    onClickItem = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}