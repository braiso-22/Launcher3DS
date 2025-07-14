package com.braiso_22.launcher3ds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
        items(
            apps,
            key = { it.id }
        ) { app ->

            val isSelected = remember(selectedApp, app) {
                selectedApp == app
            }

            val onClickCallback = remember(app, onClickItem) {
                { onClickItem(app) }
            }
            IconComponent(
                name = app.name,
                painter = app.icon,
                selected = isSelected,
                onClick = onClickCallback
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
                    selectedApp = Icon(
                        name = "Lavern Cabrera1", id = "Tamara Trevino", icon = null

                    ),
                    apps = List(10) {
                        Icon(
                            name = "Lavern Cabrera${it}",
                            id = "Tamara Trevino${it}",
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