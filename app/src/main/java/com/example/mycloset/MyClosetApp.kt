package com.example.mycloset

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mycloset.data.AppRepository
import com.example.mycloset.ui.theme.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClosetApp() {
    val navController = rememberNavController()
    val repository = remember { AppRepository() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Closet") },
                    label = { Text("Closet") },
                    selected = currentDestination?.hierarchy?.any { it.route == "closet" } == true,
                    onClick = {
                        navController.navigate("closet") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Build, contentDescription = "Lookbook") },
                    label = { Text("Lookbook") },
                    selected = currentDestination?.hierarchy?.any { it.route == "lookbook" } == true,
                    onClick = {
                        navController.navigate("lookbook") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Categories") },
                    label = { Text("Categories") },
                    selected = currentDestination?.hierarchy?.any { it.route == "categories" } == true,
                    onClick = {
                        navController.navigate("categories") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "closet",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("closet") {
                ClosetScreen(repository = repository)
            }
            composable("lookbook") {
                LookbookScreen(repository = repository)
            }
            composable("categories") {
                CategoriesScreen(repository = repository)
            }
        }
    }
}
