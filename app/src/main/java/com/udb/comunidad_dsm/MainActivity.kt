package com.udb.comunidad_dsm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.udb.comunidad_dsm.ui.HomeScreen
import com.udb.comunidad_dsm.ui.LoginScreen
import com.udb.comunidad_dsm.ui.theme.Proyecto2DSMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Proyecto2DSMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    Proyecto2DSMTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        // Change the variable to this and use Overview as a backup screen if this returns null
        val currentScreen = screens.find { it.route == currentDestination?.route } ?: Login
        Scaffold(
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Home.route) {
                    HomeScreen(
                        navigateTo = { route ->
                            navController.navigateSingleTopTo(route)
                        }
                    )
                }

                composable(route = Login.route) {
                    LoginScreen(
                        navigateTo = { route ->
                            navController.navigateSingleTopTo(route)
                        }
                    )
                }
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }