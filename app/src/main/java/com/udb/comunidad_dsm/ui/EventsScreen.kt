package com.udb.comunidad_dsm.ui

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.udb.comunidad_dsm.Login
import com.udb.comunidad_dsm.R
import com.udb.comunidad_dsm.db.dto.EventForm
import com.udb.comunidad_dsm.db.getEvents

@Composable
fun EventsScreen(navigateTo: (route: String) -> Unit, auth: FirebaseAuth) {
    val usuario = auth.currentUser;
    var events by remember { mutableStateOf<List<EventForm>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        getEvents(
            onSuccess = { eventsList ->
                events = eventsList
                isLoading = false
            },
            onFailure = {
                isLoading = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = if (isLoading) Alignment.Center else Alignment.TopCenter
    ) {
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
                Text(
                    "Eventos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 15.dp)
                )
                // Display the list of events
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 50.dp)
                        //.background(color = androidx.compose.ui.graphics.Color.Blue)
                ) {
                    items(events!!.size) { index ->
                        val event = events!![index]
                        Divider(color = androidx.compose.ui.graphics.Color.Gray, thickness = 1.dp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            BasicText(event.description, modifier = Modifier.weight(1f))
                            Icon(
                                painter = painterResource(id = R.drawable.chevron_right),
                                contentDescription = "Modificar",
                            )
                        }
                        Divider(color = androidx.compose.ui.graphics.Color.Gray, thickness = 1.dp)
                    }
                }
            }
        }
    }
}