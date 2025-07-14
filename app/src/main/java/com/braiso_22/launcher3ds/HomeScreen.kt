package com.braiso_22.launcher3ds

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.graphics.drawable.toBitmap


data class Icon(
    val id: String,
    val name: String,
    val icon: BitmapPainter?,
) {
    fun launch(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(id) ?: return
        context.startActivity(intent)
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val apps = remember { mutableListOf<Icon>() }
    var selectedIcon by remember { mutableStateOf<Icon?>(null) }
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
            val imageBitmap = resolveInfo.loadIcon(packageManager).toBitmap().asImageBitmap()
            val icon = BitmapPainter(imageBitmap)
            Icon(
                name = resolveInfo.loadLabel(packageManager).toString(),
                id = resolveInfo.activityInfo.packageName,
                icon = icon,
            )
        }
        if (newApps.isNotEmpty()) {
            selectedIcon = newApps.first()
        }
        apps.addAll(newApps)
        loading = false
    }

    var rows by rememberSaveable { mutableIntStateOf(2) }
    HomeContent(
        loadingIcons = loading,
        selectedIcon = selectedIcon,
        icons = apps,
        rows = rows,
        setRows = { newRows ->
            if (newRows in 1..7) {
                rows = newRows
            }
        },
        onClickItem = {
            if (selectedIcon == it) {
                it.launch(context)
            } else {
                selectedIcon = it
            }
        },
        onClickSettings = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    loadingIcons: Boolean,
    rows: Int,
    selectedIcon: Icon?,
    icons: List<Icon>,
    setRows: (Int) -> Unit,
    onClickItem: (Icon) -> Unit,
    onClickSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClickSettings) {
                        Icon(
                            Icons.Default.Settings,
                            "Navigate to settings"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        setRows(rows - 1)
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Less rows"
                        )
                    }
                    IconButton(onClick = {
                        setRows(rows + 1)
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "More rows"
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
                Text(
                    text = selectedIcon?.name ?: "No selected app",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (loadingIcons) {
                Text(
                    "Loading apps",
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                )
            } else {
                IconsGridComp(
                    selectedApp = selectedIcon,
                    apps = icons,
                    rows = rows,
                    onClickItem = onClickItem,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeContentPreview() {
    MaterialTheme {
        HomeContent(
            loadingIcons = false,
            selectedIcon = null,
            icons = List(20) {
                Icon(
                    name = "App $it",
                    id = "com.example.app$it",
                    icon = null,
                )
            },
            rows = 2,
            setRows = {},
            onClickItem = {},
            onClickSettings = {}
        )
    }
}
