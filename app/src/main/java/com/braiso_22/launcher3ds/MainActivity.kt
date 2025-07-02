package com.braiso_22.launcher3ds

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.braiso_22.launcher3ds.ui.theme.Launcher3DsTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Launcher3DsTheme {
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
                    val activities: List<ResolveInfo> =
                        context.packageManager.queryIntentActivities(intent, flags)
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
                LauncherScreen(
                    loading = loading,
                    selectedApp = selectedApp,
                    appList = apps,
                    onClick = {
                        if (selectedApp == it) {
                            it.launch(context)
                        } else {
                            selectedApp = it
                        }
                    }
                )
            }
        }
    }
}


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherScreen(
    loading: Boolean,
    selectedApp: App?,
    appList: List<App>,
    onClick: (App) -> Unit,
) {
    var rows by rememberSaveable { mutableIntStateOf(2) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Launcher 3DS") },
                actions = {
                    IconButton(onClick = {
                        if (rows > 1)
                            rows--
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Add rows"
                        )
                    }
                    IconButton(onClick = {
                        if (rows < 6)
                            rows++
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Add rows"
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                size.height
            },
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

                // Calcula el tamaño de fila en dp usando la densidad
                val rowSizeDp = with(density) {
                    // Convertir la altura total a dp
                    val totalHeightDp = gridHeight.toDp()

                    // Restar el padding superior e inferior (2 veces el contentPadding)
                    val availableHeight = totalHeightDp - (contentPadding * 2)

                    // Restar el espacio entre filas ((rows-1) veces el verticalSpacing)
                    val heightWithoutSpacing = availableHeight - (verticalSpacing * (rows - 1))

                    // Dividir entre el número de filas
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
                        Box(
                            modifier = Modifier
                                .size(rowSizeDp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 8.dp,
                                        color = if (app == selectedApp) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainer
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                onClick = { onClick(app) }
                            ) {
                                if (painter != null) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painter,
                                            contentDescription = app.name,
                                            modifier = Modifier
                                                .padding(10.dp) // Add 10dp padding to make content smaller than card
                                                .fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LauncherScreenPreview() {
    MaterialTheme {
        LauncherScreen(
            loading = false,
            selectedApp = null,
            appList = List(20) {
                App(
                    name = "App $it",
                    packageName = "com.example.app$it",
                    icon = null // Replace with a drawable resource if needed
                )
            },
            onClick = {}
        )
    }
}
