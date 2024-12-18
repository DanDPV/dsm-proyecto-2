package com.udb.comunidad_dsm.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Range
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import com.udb.comunidad_dsm.R
import com.udb.comunidad_dsm.db.addComment
import com.udb.comunidad_dsm.db.confirmParticipation
import com.udb.comunidad_dsm.db.dto.Comment
import com.udb.comunidad_dsm.db.addEvent
import com.udb.comunidad_dsm.db.dto.Event
import com.udb.comunidad_dsm.db.dto.EventForm
import com.udb.comunidad_dsm.db.updateEvent
import com.udb.comunidad_dsm.mergeDateAndTimeToIso
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsFormScreen(
    navigateTo: (route: String) -> Unit,
    auth: FirebaseAuth,
    backgroundColor: Color,
    navController: NavHostController,
    existingEvent: EventForm? = null,
) {
    val usuario = auth.currentUser
    var selectedDate by remember { mutableStateOf<String>("") }
    var selectedLocalDate by remember { mutableStateOf<LocalDate>(existingEvent?.localDate ?: LocalDate.now()) }
    var selectedDateError by remember { mutableStateOf(false) }
    val calendarState = rememberUseCaseState()
    val disabledDates = (0L..ChronoUnit.DAYS.between(
        LocalDate.now().minusMonths(1),
        LocalDate.now().plusDays(1)
    )).map { days ->
        LocalDate.now().minusMonths(10).plusDays(days)
    }
    val calendarBoundary = (LocalDate.now().plusDays(1)..LocalDate.now().plusYears(100))

    var selectedTime by remember { mutableStateOf<String>("") }
    var selectedLocalTime by remember { mutableStateOf<LocalTime>(existingEvent?.localTime ?: LocalTime.now()) }
    var selectedTimeError by remember { mutableStateOf(false) }
    val clockState = rememberUseCaseState()

    var descripcion by remember { mutableStateOf(existingEvent?.description ?: "") }
    var descripcionError by remember { mutableStateOf(false) }
    var ubicacion by remember { mutableStateOf(existingEvent?.location ?: "") }
    var ubicacionError by remember { mutableStateOf(false) }

    val openDialog = remember { mutableStateOf(false) }

    var showEditForm = remember { mutableStateOf(false) }
    var openConfirmationDialog = remember { mutableStateOf(false) }

    // Nuevos estados para comentar
    val openCommentDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun validarDescripcion() {
        descripcionError = descripcion.isEmpty()
    }

    fun validarUbicacion() {
        ubicacionError = ubicacion.isEmpty()
    }

    fun validarSelectedDate() {
        selectedDateError = selectedDate.isEmpty()
    }

    fun validarSelectedTime() {
        selectedTimeError = selectedTime.isEmpty()
    }

    fun handleSaveEvent() {
        validarDescripcion()
        validarUbicacion()
        validarSelectedDate()
        validarSelectedTime()

        if (!descripcionError && !ubicacionError && !selectedDateError && !selectedTimeError) {
            val isoDate = mergeDateAndTimeToIso(date = selectedLocalDate, time = selectedLocalTime)
            val event = Event(
                id = existingEvent?.id,
                date = isoDate,
                location = ubicacion,
                description = descripcion
            )

            if (existingEvent == null) {
                addEvent(
                    event = event,
                    onSuccess = { documentId ->
                        navController.popBackStack()
                    },
                    onFailure = { exception ->
                        println("No se pudo añadir el evento: ${exception.message}")
                        openDialog.value = true
                    }
                )
            } else {
                updateEvent(event,
                    onSuccess = {
                        navController.popBackStack()
                    },
                    onFailure = {
                        openDialog.value = true
                    }
                )
            }
        }
    }

    fun confirmParticipationHandler() {
        if(existingEvent != null && usuario != null) {
            confirmParticipation(existingEvent.id, usuario.uid,
                onSuccess = {
                    openConfirmationDialog.value = true
                },
                onFailure = {
                    navController.popBackStack()
                }
            )
        }
    }

    fun shareEvent(event: EventForm?) {
        if (event != null) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Evento: ${event.description}\n" +
                            "Fecha: ${event.localDate}\n" +
                            "Ubicación: ${event.location}"
                )
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Compartir evento"))
        }
    }

    LaunchedEffect(existingEvent) {
        if (existingEvent != null) {
            val date =
                Date.from(existingEvent.localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val dateString =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            selectedDate = dateString
            selectedLocalDate = existingEvent.localDate
            selectedLocalTime = existingEvent.localTime
            val localDateTime = LocalDateTime.of(LocalDate.now(), selectedLocalTime)
            val dateTime =
                Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
            val timeString =
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dateTime)
            selectedTime = timeString
            descripcion = existingEvent.description
            ubicacion = existingEvent.location
        }
    }

    // Vista principal (no edición)
    if(!showEditForm.value && existingEvent != null) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(text = "Eventos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = backgroundColor,
            )
        }) { innerPadding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top
            ){
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 40.dp)
                ){
                    Button(onClick = { confirmParticipationHandler() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(painter = painterResource(id = R.drawable.calendar_today), contentDescription = "Confirmar")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Confirmar participación")
                    }
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 40.dp)
                ){
                    Button(onClick = { showEditForm.value = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(painter = painterResource(id = R.drawable.add), contentDescription = "Editar")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Editar evento")
                    }
                }

                // Botón Comentar (debajo de Editar)
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 40.dp)
                ){
                    Button(onClick = { openCommentDialog.value = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Comentar")
                    }
                }

                // Botón Compartir (debajo de Comentar)
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 40.dp)
                ){
                    Button(onClick = { shareEvent(existingEvent) }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Compartir")
                    }
                }
            }
        }
    }

    // Confirmar participación
    if(openConfirmationDialog.value) {
        AlertDialog(
            title = { Text("Exito!!") },
            text = { Text("Hemos confirmado exitosamente tu participación") },
            onDismissRequest = {
                openConfirmationDialog.value = false
                navController.popBackStack()
            },
            confirmButton = {
                Button(onClick = {
                    openConfirmationDialog.value = false
                    navController.popBackStack()
                }) {
                    Text(text = "Entendido!")
                }
            }
        )
    }

    // Vista de edición o creación de evento
    if(existingEvent == null || showEditForm.value) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = if (existingEvent == null) "Agregar Evento" else "Actualizar Evento", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_back),
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                    },
                    backgroundColor = backgroundColor,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 40.dp)
                ) {
                    Text(
                        text = "Fecha:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(text = selectedDate, modifier = Modifier.padding(end = 5.dp))
                    CalendarDialog(
                        state = calendarState,
                        config = CalendarConfig(
                            yearSelection = true,
                            monthSelection = true,
                            style = CalendarStyle.MONTH,
                            disabledDates = disabledDates,
                            boundary = calendarBoundary,
                        ),
                        selection = CalendarSelection.Date { newDate ->
                            selectedLocalDate = newDate
                            val date =
                                Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                            val dateString =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                            selectedDate = dateString
                        }
                    )
                    Button(onClick = {
                        calendarState.show()
                    }) {
                        Text(text = "Seleccionar fecha")
                    }
                }
                Text(
                    text = if (selectedDateError) "Fecha es requerida" else "",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Hora:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(text = selectedTime, modifier = Modifier.padding(end = 5.dp))
                    ClockDialog(
                        state = clockState,
                        selection = ClockSelection.HoursMinutes { hours, minutes ->
                            val tmpSelectedLocalTime = LocalTime.of(hours, minutes, 0)
                            selectedLocalTime = tmpSelectedLocalTime
                            val localDateTime = LocalDateTime.of(LocalDate.now(), selectedLocalTime)
                            val date =
                                Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
                            val dateString =
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
                            selectedTime = dateString
                        },
                        config = ClockConfig(
                            is24HourFormat = false,
                            defaultTime = selectedLocalTime
                        )
                    )
                    Button(onClick = {
                        clockState.show()
                    }) {
                        Text(text = "Seleccionar hora")
                    }
                }
                Text(
                    text = if (selectedTimeError) "Hora es requerida" else "",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                        validarDescripcion()
                    },
                    label = { Text("Descripción") },
                    isError = descripcionError,
                    supportingText = {
                        if (descripcionError) {
                            Text("Este campo es requerido")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = {
                        ubicacion = it
                        validarUbicacion()
                    },
                    label = { Text("Ubicación") },
                    isError = ubicacionError,
                    supportingText = {
                        if (ubicacionError) {
                            Text("Este campo es requerido")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
                Button(
                    onClick = {
                        handleSaveEvent()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp),
                ) {
                    Text(if (existingEvent == null) "Agregar Evento" else "Actualizar Evento", modifier = Modifier.padding(vertical = 5.dp))
                }
                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = { Text(text = "Error") },
                        text = { Text(text = "No se pudo realizar la acción, por favor intente de nuevo más tarde.") },
                        confirmButton = {
                            TextButton(onClick = { openDialog.value = false }) { Text("Ok") }
                        },
                        dismissButton = {
                            TextButton(onClick = { openDialog.value = false }) { Text("Cerrar") }
                        }
                    )
                }
            }
        }
    }

    // Agregar comentario
    if (openCommentDialog.value) {
        var commentText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { openCommentDialog.value = false },
            title = { Text("Agregar Comentario") },
            text = {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (existingEvent?.id != null) {
                        val comment = Comment(
                            eventId = existingEvent.id,
                            userId = usuario?.uid ?: "",
                            userName = usuario?.displayName ?: "Anónimo",
                            content = commentText
                        )
                        addComment(
                            comment,
                            onSuccess = {
                                openCommentDialog.value = false
                                println("Comentario agregado exitosamente")
                            },
                            onFailure = { e ->
                                println("Error al agregar comentario: ${e.message}")
                            }
                        )
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { openCommentDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}