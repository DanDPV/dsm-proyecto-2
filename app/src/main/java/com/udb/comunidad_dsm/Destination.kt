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

interface DestinationMenu: Destination {
    val icon: Int
    val title: String
}

/**
 * Navigation destinations
 */
object Home : Destination {
    override val route = "home"
}

object HomeMenu: DestinationMenu {
    override val route = Home.route
    override val icon = R.drawable.home_white
    override val title = "Inicio"
}

object Login : Destination {
    override val route = "login"
}

object Events : Destination {
    override val route = "events"
}

object EventsMenu: DestinationMenu {
    override val route = Events.route
    override val icon = R.drawable.calendar_today_white
    override val title = "Eventos"
}

object Configuration : Destination {
    override val route = "configuration"
}

object ConfigurationMenu: DestinationMenu {
    override val route = Configuration.route
    override val icon = R.drawable.person_white
    override val title = "Usuario"
}

val screens = listOf(Home, Login, Events, Configuration)
val menuScreens = listOf(HomeMenu, EventsMenu, ConfigurationMenu)