package com.udb.comunidad_dsm

import androidx.compose.runtime.Composable
import com.udb.comunidad_dsm.ui.HomeScreen
import com.udb.comunidad_dsm.ui.LoginScreen

/**
 * Contract for information needed on every navigation destination
 */
interface Destination {
    val route: String
}

/**
 * Navigation destinations
 */
object Home : Destination {
    override val route = "home"
}

object Login : Destination {
    override val route = "login"
}

val screens = listOf(Home, Login)