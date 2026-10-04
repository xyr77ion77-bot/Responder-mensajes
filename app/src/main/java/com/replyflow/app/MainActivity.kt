package com.replyflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.replyflow.app.ui.*
import com.replyflow.app.ui.theme.Nav
import com.replyflow.app.ui.theme.ReplyFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as ReplyFlowApp
        setContent { ReplyFlowTheme { ReplyFlowNav(app) } }
    }
}

private data class Destination(val route: String, val glyph: String, val label: String)
private val mainDestinations = listOf(
    Destination("home", "+", "Inicio"), Destination("rules", "●", "Respuestas"),
    Destination("reports", "▥", "Informes"), Destination("settings", "⚙", "Ajustes")
)

@Composable private fun ReplyFlowNav(app: ReplyFlowApp) {
    val nav = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route.orEmpty()
    val showBottom = route in mainDestinations.map { it.route }
    Scaffold(
        containerColor = com.replyflow.app.ui.theme.Bg,
        bottomBar = {
            if (showBottom) NavigationBar(containerColor = Nav) {
                mainDestinations.forEach { item ->
                    NavigationBarItem(
                        selected = route == item.route,
                        onClick = { nav.navigate(item.route) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                        icon = { Text(item.glyph) }, label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF3F4270))
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = "home", modifier = Modifier.padding(if (showBottom) padding else androidx.compose.foundation.layout.PaddingValues())) {
            composable("home") {
                val vm: HomeViewModel = viewModel(factory = SimpleFactory { HomeViewModel(app.rules) })
                HomeScreen(vm, onCreate = { nav.navigate("editor/-1") }, onRules = { nav.navigate("rules") }, onReports = { nav.navigate("reports") }, onHelp = { nav.navigate("help") }, onPermission = { openNotificationAccess(context) })
            }
            composable("rules") {
                val vm: RulesViewModel = viewModel(factory = SimpleFactory { RulesViewModel(app.rules, app.settings) })
                RulesScreen(vm, onCreate = { nav.navigate("editor/-1") }, onEdit = { nav.navigate("editor/$it") }, onPermission = { openNotificationAccess(context) })
            }
            composable("editor/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: -1
                val vm: EditorViewModel = viewModel(factory = SimpleFactory { EditorViewModel(app.rules, app.aiConfig) })
                RuleEditorScreen(vm, id, onBack = { nav.popBackStack() }, onAi = { nav.navigate("ai") })
            }
            composable("ai") {
                val vm: AiViewModel = viewModel(factory = SimpleFactory { AiViewModel(app.aiConfig) })
                AiWizardScreen(vm, onBack = { nav.popBackStack() }, onFinished = { nav.popBackStack() })
            }
            composable("reports") {
                val vm: ReportsViewModel = viewModel(factory = SimpleFactory { ReportsViewModel(app.rules) })
                ReportsScreen(vm)
            }
            composable("help") { HelpScreen(onBack = { nav.popBackStack() }) }
            composable("settings") {
                val vm: SettingsViewModel = viewModel(factory = SimpleFactory { SettingsViewModel(app.rules, app.settings) })
                SettingsScreen(vm, onPermission = { openNotificationAccess(context) })
            }
        }
    }
}

private fun openNotificationAccess(context: android.content.Context) {
    context.startActivity(android.content.Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
}
