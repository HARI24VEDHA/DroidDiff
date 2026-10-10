package com.example.mediverify.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mediverify.ui.about.AboutScreen
import com.example.mediverify.ui.history.HistoryScreen
import com.example.mediverify.ui.home.HomeScreen
import com.example.mediverify.ui.manual.ManualEntryScreen
import com.example.mediverify.ui.result.VerificationResultScreen
import com.example.mediverify.ui.scan.ScannerScreen
import com.example.mediverify.ui.viewmodel.MediVerifyViewModel

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    data object Scan : BottomNavItem("scan", "Scan", Icons.Default.QrCodeScanner)
    data object History : BottomNavItem("history", "History", Icons.Default.History)
    data object About : BottomNavItem("about", "About", Icons.Default.Info)
}

@Composable
fun AppNavigation(viewModel: MediVerifyViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Scan,
        BottomNavItem.History,
        BottomNavItem.About,
    )

    Scaffold(
        bottomBar = {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route
            if (currentRoute != "result") {
                BottomAppBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = viewModel,
                )
            }
            composable(BottomNavItem.Scan.route) {
                ScannerScreen(navController = navController, viewModel = viewModel)
            }
            composable(BottomNavItem.History.route) {
                HistoryScreen(navController = navController, viewModel = viewModel)
            }
            composable(BottomNavItem.About.route) {
                AboutScreen(navController = navController)
            }
            composable("manual") {
                ManualEntryScreen(navController = navController, viewModel = viewModel)
            }
            composable("result") {
                VerificationResultScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}
