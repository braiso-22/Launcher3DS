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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark


sealed interface Icon {
    val name: String
    val icon: Drawable?

    data class App(
        val packageName: String,
        override val name: String,
        override val icon: Drawable?,
    ) : Icon {
        fun launch(context: Context) {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
            context.startActivity(intent)
        }
    }

    data class Empty(
        override val name: String,
        override val icon: Drawable?,
    ) : Icon

    data class Folder(
        override val name: String,
        override val icon: Drawable?,
    ) : Icon
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
            Icon.App(
                name = resolveInfo.loadLabel(packageManager).toString(),
                packageName = resolveInfo.activityInfo.packageName,
                icon = resolveInfo.loadIcon(packageManager)
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
            when (it) {
                is Icon.App -> {
                    if (selectedIcon == it) {
                        it.launch(context)
                    } else {
                        selectedIcon = it
                    }
                }

                is Icon.Empty -> TODO()
                is Icon.Folder -> TODO()
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
                Text(text = selectedIcon?.name ?: "No selected app")
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
                Icon.App(
                    name = "App $it",
                    packageName = "com.example.app$it",
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
