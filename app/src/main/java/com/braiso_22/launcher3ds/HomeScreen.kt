package com.braiso_22.launcher3ds

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

data class App(
    val name: String,
    val packageName: String,
    val icon: Drawable?,
) {
    fun launch(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
        context.startActivity(intent)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val apps = remember { mutableListOf<App>() }
    var selectedApp by remember { mutableStateOf<App?>(null) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        loading = true
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val flags = PackageManager.ResolveInfoFlags.of(
            PackageManager.MATCH_ALL.toLong(),
        )
        val packageManager = context.packageManager
        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(intent, flags)
        val newApps = activities.map { resolveInfo ->
            App(
                name = resolveInfo.loadLabel(packageManager).toString(),
                packageName = resolveInfo.activityInfo.packageName,
                icon = resolveInfo.loadIcon(packageManager)
            )
        }
        if (newApps.isNotEmpty()) {
            selectedApp = newApps.first()
        }
        apps.addAll(newApps)
        loading = false
    }
    var rows by rememberSaveable { mutableIntStateOf(2) }
    HomeContent(
        loading = loading,
        selectedApp = selectedApp,
        appList = apps,
        rows = rows,
        setRows = { newRows ->
            if (newRows in 1..6) {
                rows = newRows
            }
        },
        onClick = {
            if (selectedApp == it) {
                it.launch(context)
            } else {
                selectedApp = it
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    loading: Boolean,
    rows: Int,
    selectedApp: App?,
    appList: List<App>,
    setRows: (Int) -> Unit,
    onClick: (App) -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Launcher 3DS") },
                actions = {
                    IconButton(onClick = {
                        setRows(rows - 1)
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Add rows"
                        )
                    }
                    IconButton(onClick = {
                        setRows(rows + 1)
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Add rows"
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        Column(Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedApp == null) {
                    Text("No selected app")
                } else {
                    Text(selectedApp.packageName)
                    Text(selectedApp.name)
                }
            }
            if (loading) {
                Text(
                    "Loading apps",
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                )
            } else {
                var gridHeight by remember { mutableIntStateOf(360) }
                val density = LocalDensity.current
                val contentPadding = 8.dp
                val verticalSpacing = 8.dp

                val rowSizeDp = with(density) {
                    val totalHeightDp = gridHeight.toDp()

                    val topAndBottomPadding = contentPadding * 2
                    val availableHeight = totalHeightDp - topAndBottomPadding

                    val spacesBetweenRows = verticalSpacing * (rows - 1)
                    val heightWithoutSpacing = availableHeight - spacesBetweenRows

                    heightWithoutSpacing / rows
                }

                LazyHorizontalGrid(
                    modifier = Modifier
                        .weight(1f)
                        .onSizeChanged { size ->
                            gridHeight = size.height
                        },
                    contentPadding = PaddingValues(contentPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                    rows = GridCells.FixedSize(rowSizeDp),
                ) {
                    items(appList) { app ->
                        val painter = remember(app.icon) {
                            app.icon?.toBitmap()?.asImageBitmap()?.let {
                                BitmapPainter(it)
                            }
                        }
                        IconComponent(
                            name = app.name,
                            painter = painter,
                            selected = selectedApp == app,
                            dpSize = rowSizeDp,
                            onClick = {
                                onClick(app)
                            }
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeContentPreview() {
    MaterialTheme {
        HomeContent(
            loading = false,
            selectedApp = null,
            appList = List(20) {
                App(
                    name = "App $it",
                    packageName = "com.example.app$it",
                    icon = null // Replace with a drawable resource if needed
                )
            },
            rows = 2,
            setRows = {},
            onClick = {}
        )
    }
}
