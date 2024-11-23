package com.udb.comunidad_dsm.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.udb.comunidad_dsm.Destination
import com.udb.comunidad_dsm.DestinationMenu
import com.udb.comunidad_dsm.R

@Composable
fun BottomNavigationBar(
    navigateTo: (route: String) -> Unit,
    currentRoute: Destination,
    screens: List<DestinationMenu>,
    backgroundColor: Color,
    secondaryColor: Color
) {
    BottomNavigation(
        backgroundColor = backgroundColor
    ) {
        screens.forEach { screen ->
            val selected = currentRoute.route == screen.route
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title,
                        tint = if (selected) secondaryColor else MaterialTheme.colors.onPrimary
                    )
                },
                label = { Text(screen.title, color = if (selected) secondaryColor else MaterialTheme.colors.onPrimary) },
                selected = selected,
                onClick = {
                    navigateTo(screen.route)
                }
            )
        }
    }
}