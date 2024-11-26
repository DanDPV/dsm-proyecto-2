package com.udb.comunidad_dsm.ui

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.udb.comunidad_dsm.Login
import com.udb.comunidad_dsm.R
import com.udb.comunidad_dsm.db.deleteEvent
import com.udb.comunidad_dsm.db.dto.EventForm
import com.udb.comunidad_dsm.db.getEvents

@Composable
fun EventsScreen(
    navigateTo: (route: String) -> Unit,
    auth: FirebaseAuth,
    navigateToEventForm: (route: String) -> Unit
) {
    val usuario = auth.currentUser;
    var events by remember { mutableStateOf<List<EventForm>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isModalVisible by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventForm?>(null) }
    val openErrorDialog = remember { mutableStateOf(false) }

    fun deleteLocalEvent(deletedEvent: EventForm) {
        if (events != null) {
            events = events!!.filter { it.id != deletedEvent.id }
        }
    }

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
                        if (index > 0) {
                            Divider(
                                color = androidx.compose.ui.graphics.Color.Gray,
                                thickness = 1.dp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .padding(horizontal = 16.dp)
                                .pointerInput(Unit) { // Detect long press
                                    detectTapGestures(
                                        onLongPress = {
                                            selectedEvent = event
                                            isModalVisible = true
                                        }
                                    )
                                }
                                .clickable {
                                    // Navigate to the EventsFormScreen in update mode
                                    navigateToEventForm(event.id)
                                }
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

                if (isModalVisible && selectedEvent != null) {
                    AlertDialog(
                        onDismissRequest = { isModalVisible = false },
                        title = { Text("Eliminar evento") },
                        text = { Text("¿Está seguro que desea eliminar el evento '${selectedEvent?.description}'?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    deleteEvent(
                                        id = selectedEvent!!.id,
                                        onSuccess = {
                                            deleteLocalEvent(selectedEvent!!)
                                        },
                                        onFailure = {
                                            openErrorDialog.value = true
                                        }
                                    ) // Perform delete action
                                    isModalVisible = false // Close modal
                                }
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { isModalVisible = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                if (openErrorDialog.value) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = {
                            // Dismiss the dialog when the user clicks outside the dialog or on the back
                            // button. If you want to disable that functionality, simply use an empty
                            // onDismissRequest.
                            openErrorDialog.value = false
                        },
                        title = { Text(text = "Error") },
                        text = { Text(text = "Hubo un error al intentar eliminar el evento.") },
                        confirmButton = {
                            TextButton(onClick = { openErrorDialog.value = false }) { Text("Ok") }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                openErrorDialog.value = false
                            }) { Text("Cerrar") }
                        }
                    )
                }
            }
        }
    }
}