import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.ui.tooling.preview.Preview

class NestedScreen(private val id: Int, private val tabId: Int) : Screen {
    override val key = "NestedScreen$id+$tabId"

    @Composable
    override fun Content() {
        val color = when (tabId % 2) {
            0 -> MaterialTheme.colors.primary
            else -> MaterialTheme.colors.secondary
        }

        Column(
            Modifier.fillMaxSize().background(color),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val navigator = LocalNavigator.currentOrThrow
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            Text("Nested Screen $id in tab $tabId")
            Button(
                onClick = { navigator.push(NestedScreen(id + 1, tabId)) },
            ) {
                Text("Push Nested Screen")
            }
            Button(
                onClick = {
                    bottomSheetNavigator.show(
                        screen = object : Screen {
                            override val key = "BottomSheetScreen$id"

                            @Composable
                            override fun Content() {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colors.secondaryVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Bottom Sheet $id")
                                }
                            }
                        }
                    )
                },
            ) {
                Text("Show Bottom Sheet")
            }
        }
    }
}

class TabScreen(private val id: Int) : Tab {
    override val key = "TabScreen$id"

    @Composable
    override fun Content() {
        Navigator(screen = NestedScreen(0, id))
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = when (id % 2) {
                0 -> Icons.Default.Home
                else -> Icons.Default.Call
            }
            return TabOptions(
                index = id.toUShort(),
                title = key,
                icon = rememberVectorPainter(icon)
            )
        }
}

val tab0 = TabScreen(0)
val tab1 = TabScreen(1)


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            BottomSheetNavigator {
                TabNavigator(tab0) {
                    Scaffold(
                        content = {
                            CurrentTab()
                        },
                        bottomBar = {
                            NavigationBar {
                                val tabNavigator = LocalTabNavigator.current
                                val index = tabNavigator.current.options.index.toInt()

                                NavigationBarItem(
                                    selected = index == 0,
                                    onClick = {
                                        tabNavigator.current = tab0
                                    },
                                    icon = { Icon(tab0.options.icon!!, "") },
                                    label = { Text(tab0.options.title) }
                                )
                                NavigationBarItem(
                                    selected = index == 1,
                                    onClick = {
                                        tabNavigator.current = tab1
                                    },
                                    icon = { Icon(tab1.options.icon!!, "") },
                                    label = { Text(tab1.options.title) }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

