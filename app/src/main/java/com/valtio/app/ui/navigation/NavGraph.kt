package com.valtio.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.valtio.app.R
import com.valtio.app.ui.dashboard.DashboardScreen
import com.valtio.app.ui.pesquisa.PesquisaScreen
import com.valtio.app.ui.produto.ProdutoDetailScreen
import com.valtio.app.ui.produto.ProdutoFormScreen
import com.valtio.app.ui.produto.ProdutoListScreen
import com.valtio.app.ui.settings.SettingsScreen
import com.valtio.app.ui.theme.Primary
import com.valtio.app.ui.theme.PrimaryLight
import com.valtio.app.ui.theme.Surface

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object ProdutoList : Screen("produto_list")
    data object ProdutoDetail : Screen("produto_detail/{produtoId}") {
        fun createRoute(produtoId: Long) = "produto_detail/$produtoId"
    }
    data object ProdutoForm : Screen("produto_form?produtoId={produtoId}") {
        fun createRoute(produtoId: Long? = null) =
            if (produtoId != null) "produto_form?produtoId=$produtoId"
            else "produto_form"
    }
    data object Pesquisa : Screen("pesquisa")
    data object Settings : Screen("settings")
    data object Export : Screen("export")
    data object About : Screen("about")
}

data class BottomNavItem(
    val screen: Screen,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun bottomNavItems() = listOf(
    BottomNavItem(
        screen = Screen.Dashboard,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    ),
    BottomNavItem(
        screen = Screen.ProdutoList,
        labelResId = R.string.nav_products,
        selectedIcon = Icons.Filled.Inventory2,
        unselectedIcon = Icons.Outlined.Inventory2
    ),
    BottomNavItem(
        screen = Screen.Pesquisa,
        labelResId = R.string.nav_search,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    BottomNavItem(
        screen = Screen.Settings,
        labelResId = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

@Composable
fun ValtioNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = bottomNavItems()
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.ProdutoList.route,
        Screen.Pesquisa.route,
        Screen.Settings.route
    )
    val showFab = currentRoute == Screen.ProdutoList.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Surface
                ) {
                    items.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        val label = stringResource(item.labelResId)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = label
                                )
                            },
                            label = { Text(text = label, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = PrimaryLight.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.ProdutoForm.createRoute())
                    },
                    containerColor = Primary,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.fab_new_product)
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onProdutoClick = { produtoId ->
                        navController.navigate(Screen.ProdutoDetail.createRoute(produtoId))
                    }
                )
            }
            composable(Screen.ProdutoList.route) {
                ProdutoListScreen(
                    onProdutoClick = { produtoId ->
                        navController.navigate(Screen.ProdutoDetail.createRoute(produtoId))
                    },
                    onEditClick = { produtoId ->
                        navController.navigate(Screen.ProdutoForm.createRoute(produtoId))
                    }
                )
            }
            composable(Screen.ProdutoDetail.route) { backStackEntry ->
                val produtoId = backStackEntry.arguments?.getString("produtoId")?.toLongOrNull()
                ProdutoDetailScreen(
                    produtoId = produtoId ?: return@composable,
                    onNavigateBack = { navController.popBackStack() },
                    onEditClick = { id ->
                        navController.navigate(Screen.ProdutoForm.createRoute(id))
                    }
                )
            }
            composable(Screen.ProdutoForm.route) { backStackEntry ->
                val produtoId = backStackEntry.arguments?.getString("produtoId")?.toLongOrNull()
                ProdutoFormScreen(
                    produtoId = produtoId,
                    onNavigateBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(Screen.Pesquisa.route) {
                PesquisaScreen(
                    onProdutoClick = { produtoId ->
                        navController.navigate(Screen.ProdutoDetail.createRoute(produtoId))
                    }
                )
            }
            composable(Screen.Settings.route) {
                val ctx = LocalContext.current
                SettingsScreen(
                    context = ctx,
                    onNavigateToExport = {
                        navController.navigate(Screen.Export.route)
                    },
                    onNavigateToAbout = {
                        navController.navigate(Screen.About.route)
                    }
                )
            }
            composable(Screen.Export.route) {
                com.valtio.app.ui.export.ExportScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.About.route) {
                com.valtio.app.ui.settings.AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
