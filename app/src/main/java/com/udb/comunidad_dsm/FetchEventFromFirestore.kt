package com.udb.comunidad_dsm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.udb.comunidad_dsm.db.dto.EventForm
import com.udb.comunidad_dsm.db.getEventById

@Composable
fun FetchEventFromFirestore(eventId: String): EventForm? {
    var event by remember { mutableStateOf<EventForm?>(null) }

    LaunchedEffect(eventId) {
        getEventById(
            id = eventId,
            onSuccess = {
                event = it
            },
            onFailure = {}
        )
    }

    return event
}
