package com.udb.comunidad_dsm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.udb.comunidad_dsm.db.dto.Event
import com.udb.comunidad_dsm.db.getCloserEvents
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(navigateTo: (route: String) -> Unit, auth: FirebaseAuth) {
    val usuario = auth.currentUser
    var isLoading by remember { mutableStateOf(true) }
    var events by remember { mutableStateOf<List<Event>?>((null)) }

    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    LaunchedEffect(Unit) {
        getCloserEvents(
            onSuccess = { eventList ->
                events = eventList
                isLoading = false
            }
        )
    }
    // Home screen UI for logged-in users
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Row {
            Text(
                "Bienvenido",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
        }
        Row {
            Text(
                "Está es la app para los eventos de tu comunidad.",
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
        Row {
            Text(
                "Eventos cercanos:",
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 35.dp),
            )
        }
        Row(modifier = Modifier.padding(horizontal = 35.dp)) {
            when {
                isLoading -> {
                    // Show a loading indicator
                    CircularProgressIndicator()
                }

                events.isNullOrEmpty() -> {
                    // Show a message if no events are available
                    Text("No se encontraron eventos.")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 50.dp)
                        //.background(color = androidx.compose.ui.graphics.Color.Blue)
                    ) {
                        items(events!!.size) { index ->
                            val event = events!![index]
                            if (index > 0) {
                                Divider(
                                    color = Color.Gray,
                                    thickness = 1.dp
                                )
                            }

                            val formattedDate = try {
                                val date = inputDateFormat.parse(event.date) // Parsear fecha original
                                outputDateFormat.format(date) // Formatear al nuevo formato
                            } catch (e: Exception) {
                                "Fecha inválida" // Manejar errores si la fecha es incorrecta
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                BasicText(event.description, modifier = Modifier.weight(1f))
                                BasicText(formattedDate, modifier = Modifier.weight(1f))
                            }
                            Divider(color = Color.Gray, thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}