package com.udb.comunidad_dsm

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Contrato para información necesaria sobre cada destino de navegación
 */
interface Destination {
    val route: String
}

interface DestinationMenu : Destination {
    val icon: Int
    val title: String
}

interface FABAction : Destination {
    val action: (navigateTo: (route: String) -> Unit) -> Unit
}

/**
 * Destinos de navegación
 */
object Home : Destination {
    override val route = "home"
}

object EventsRecord : Destination {
    override val route = "events-record"
}

object HomeMenu : DestinationMenu {
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

object EventsForm : Destination {
    override val route = "events-form"
    const val idTypeArg = "id"
    val routeWithArgs = "${route}/{${idTypeArg}}"
    val arguments = listOf(
        navArgument(idTypeArg) { type = NavType.StringType; nullable = true }
    )
}

object EventsMenu : DestinationMenu {
    override val route = Events.route
    override val icon = R.drawable.calendar_today_white
    override val title = "Eventos"
}

object EventsFABAction : FABAction {
    override val route = Events.route
    override val action: (navigateTo: (route: String) -> Unit) -> Unit = {
        it(EventsForm.route)
    }
}

object Configuration : Destination {
    override val route = "configuration"
}

object ConfigurationMenu : DestinationMenu {
    override val route = Configuration.route
    override val icon = R.drawable.person_white
    override val title = "Usuario"
}

object EventsRecordMenu : DestinationMenu {
    override val route = EventsRecord.route
    override val icon = R.drawable.calendar_today_white
    override val title = "Historial de eventos"
}

/**
 * Nueva pantalla de comentarios
 */
object Comments : Destination {
    override val route = "comments"
    const val eventIdArg = "eventId"
    val routeWithArgs = "${route}/{${eventIdArg}}"
    val arguments = listOf(
        navArgument(eventIdArg) { type = NavType.StringType }
    )
}

val screens = listOf(Home, Login, Events, EventsRecord, Configuration, EventsForm, Comments)
val menuScreens = listOf(HomeMenu, EventsMenu, EventsRecordMenu, ConfigurationMenu)
val fabScreens = listOf(Events)
val fabActions = listOf(EventsFABAction)