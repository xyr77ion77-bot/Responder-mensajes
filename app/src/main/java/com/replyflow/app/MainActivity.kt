package com.replyflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.replyflow.app.ui.*
import com.replyflow.app.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as ReplyFlowApp
        setContent { ReplyFlowTheme { ReplyFlowNav(app) } }
    }
}

private data class Destination(val route: String, val icon: ImageVector, val label: String, val caption: String)
private val destinations = listOf(
    Destination("home", Icons.Default.Home, "Inicio", "Resumen y acceso rápido"),
    Destination("rules", Icons.Default.Edit, "Respuestas", "Reglas y automatizaciones"),
    Destination("reports", Icons.Default.Star, "Informes", "Actividad y resultados"),
    Destination("help", Icons.Default.Info, "Ayuda", "Guías y solución de problemas"),
    Destination("settings", Icons.Default.Settings, "Ajustes", "Permisos, datos y privacidad")
)

@Composable private fun ReplyFlowNav(app: ReplyFlowApp) {
    val nav = rememberNavController()
    val context = LocalContext.current
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route.orEmpty()
    val openDrawer = { scope.launch { drawer.open() }; Unit }

    ModalNavigationDrawer(
        drawerState = drawer,
        gesturesEnabled = route in destinations.map { it.route },
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(312.dp),
                drawerContainerColor = Nav,
                drawerShape = RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp)
            ) {
                Row(Modifier.fillMaxWidth().statusBarsPadding().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(48.dp), shape = RoundedCornerShape(16.dp), color = Blurple) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Send, null, tint = androidx.compose.ui.graphics.Color.White) }
                    }
                    Column(Modifier.padding(start = 13.dp)) {
                        Text("ReplyFlow", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Respuestas automáticas", color = Muted, fontSize = 13.sp)
                    }
                }
                HorizontalDivider(color = Line)
                Spacer(Modifier.height(12.dp))
                destinations.forEach { item ->
                    NavigationDrawerItem(
                        selected = route == item.route,
                        onClick = {
                            nav.navigate(item.route) { launchSingleTop = true; popUpTo("home") { saveState = true }; restoreState = true }
                            scope.launch { drawer.close() }
                        },
                        icon = { Icon(item.icon, null) },
                        label = { Column { Text(item.label, fontWeight = FontWeight.Bold); Text(item.caption, color = Muted, fontSize = 12.sp) } },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = ColorDiscordSelected)
                    )
                }
                Spacer(Modifier.weight(1f))
                Surface(color = Surface, modifier = Modifier.padding(14.dp), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).background(Green, RoundedCornerShape(50)))
                            Text(" Servicio local activo", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Sin telemetría · datos en tu dispositivo", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                }
                Text("ReplyFlow 0.1.0", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp).navigationBarsPadding())
            }
        }
    ) {
        CompositionLocalProvider(LocalOpenDrawer provides openDrawer) {
            NavHost(navController = nav, startDestination = "home", modifier = Modifier.fillMaxSize()) {
                composable("home") {
                    val vm: HomeViewModel = viewModel(factory = SimpleFactory { HomeViewModel(app.rules) })
                    HomeScreen(vm, { nav.navigate("editor/-1") }, { nav.navigate("rules") }, { nav.navigate("reports") }, { nav.navigate("help") }, { openNotificationAccess(context) })
                }
                composable("rules") {
                    val vm: RulesViewModel = viewModel(factory = SimpleFactory { RulesViewModel(app.rules, app.settings) })
                    RulesScreen(vm, { nav.navigate("editor/-1") }, { nav.navigate("editor/$it") }, { openNotificationAccess(context) })
                }
                composable("editor/{id}") { stack ->
                    val id = stack.arguments?.getString("id")?.toLongOrNull() ?: -1
                    val vm: EditorViewModel = viewModel(factory = SimpleFactory { EditorViewModel(app.rules, app.aiConfig) })
                    RuleEditorScreen(vm, id, { nav.popBackStack() }, { nav.navigate("ai") })
                }
                composable("ai") {
                    val vm: AiViewModel = viewModel(factory = SimpleFactory { AiViewModel(app.aiConfig) })
                    AiWizardScreen(vm, { nav.popBackStack() }, { nav.popBackStack() })
                }
                composable("reports") {
                    val vm: ReportsViewModel = viewModel(factory = SimpleFactory { ReportsViewModel(app.rules) })
                    ReportsScreen(vm)
                }
                composable("help") { HelpScreen() }
                composable("settings") {
                    val vm: SettingsViewModel = viewModel(factory = SimpleFactory { SettingsViewModel(app.rules, app.settings) })
                    SettingsScreen(vm) { openNotificationAccess(context) }
                }
            }
        }
    }
}

private fun openNotificationAccess(context: android.content.Context) {
    context.startActivity(android.content.Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
}
