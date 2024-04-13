package com.document.scanner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.document.scanner.R
import com.document.scanner.data.ScannedFile
import com.document.scanner.navigation.DocumentScannerDestination

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScannerApp(
    onClickScanButton: () -> Unit,
    scannedFiles: MutableList<ScannedFile>
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState());
//    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .safeDrawingPadding(),
        topBar = { DocumentScannerTopAppBar() },
//        bottomBar = { DocumentScannerBottomAppBar(navController = navController) },
        floatingActionButton = { ScanButton(onClickScanButton) },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
//            BottomNavGraph(
//                navController = navController,
//                scannedDocumentsFiles,
//                scannedImagesFiles,
//                it
//            )

            ScannedFilesScreen(scannedFiles = scannedFiles, it)
        }
    }
}

//@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScannerTopAppBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier
            .fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    )
}


@Composable
fun DocumentScannerBottomAppBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomScreensDestination = listOf(
        DocumentScannerDestination.Documents,
        DocumentScannerDestination.Images
    )

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary,
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        bottomScreensDestination.forEach {
            AddBottomNavigationItem(
                screen = it,
                navController = navController,
                currentDestination = currentDestination
            )
        }
    }
}

@Composable
fun RowScope.AddBottomNavigationItem(
    screen: DocumentScannerDestination,
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val isSelected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true

    BottomNavigationItem(
        label = {
            Text(
                text = stringResource(id = screen.label),
                style = TextStyle.Default,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = if (isSelected) screen.selectedIconId else screen.unSelectedIconId),
                contentDescription = stringResource(id = screen.label)
            )
        }, selectedContentColor = Color.Black,
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        selected = isSelected,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        })
}

@Preview()
@Composable
fun ScanButton(onScan: () -> Unit = {}) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )

    Button(
        onClick = onScan,
        modifier = Modifier
            .width(100.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
        ) {
            Text(
                text = stringResource(id = R.string.scan),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}