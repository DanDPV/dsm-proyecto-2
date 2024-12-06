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
import com.udb.comunidad_dsm.db.dto.EventForm

@Composable
fun EventsRecordScreen(
    navigateTo: (route: String) -> Unit,
    auth: FirebaseAuth
){
    val usuario = auth.currentUser;
    var events by remember { mutableStateOf<List<EventForm>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {

    }
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
    }
}