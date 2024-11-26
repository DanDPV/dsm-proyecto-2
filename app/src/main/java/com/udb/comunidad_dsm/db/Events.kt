package com.udb.comunidad_dsm.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.udb.comunidad_dsm.db.dto.Event
import com.udb.comunidad_dsm.db.dto.EventForm
import com.udb.comunidad_dsm.parseIsoStringToDateAndTime
import java.time.LocalDate
import java.time.LocalTime

fun addEvent(event: Event, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection(EVENTS_COLLECTION)
        .add(event)
        .addOnSuccessListener { documentReference ->
            onSuccess(documentReference.id) // Return document ID on success
        }
        .addOnFailureListener { e ->
            onFailure(e) // Pass the exception to the caller
        }
}

fun updateEvent(event: Event, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection(EVENTS_COLLECTION)
        .document(event.id!!)
        .set(event)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { e ->
            onFailure(e) // Pass the exception to the caller
        }
}

fun getEventById(id: String, onSuccess: (EventForm) -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection(EVENTS_COLLECTION)
        .document(id)
        .get()
        .addOnSuccessListener {document ->
            val event = document.toObject(Event::class.java) // Convert Firestore document to Event object
            val eventForm = EventForm(
                location = event?.location ?: "",
                description = event?.description ?: "",
                localDate = LocalDate.now(),
                localTime = LocalTime.now(),
                id = document.id,
            )
            if (event != null) {
                val (date, time) = parseIsoStringToDateAndTime(event.date)
                eventForm.localDate = date
                eventForm.localTime = time
            }

            onSuccess(eventForm)
        }
        .addOnFailureListener { e ->
            onFailure(e) // Pass the exception to the caller
        }
}

fun getEvents(onSuccess: (List<EventForm>) -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection(EVENTS_COLLECTION)
        .get()
        .addOnSuccessListener { result ->
            val events = result.documents.mapNotNull { document ->
                val event = document.toObject(Event::class.java) // Convert Firestore document to Event object
                val eventForm = EventForm(
                    location = event?.location ?: "",
                    description = event?.description ?: "",
                    localDate = LocalDate.now(),
                    localTime = LocalTime.now(),
                    id = document.id,
                )
                if (event != null) {
                    val (date, time) = parseIsoStringToDateAndTime(event.date)
                    eventForm.localDate = date
                    eventForm.localTime = time
                }

                eventForm
            }
            onSuccess(events)
        }
        .addOnFailureListener { e ->
            onFailure(e) // Pass the exception to the caller
        }
}

fun deleteEvent(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection(EVENTS_COLLECTION)
        .document(id)
        .delete()
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { e ->
            onFailure(e) // Pass the exception to the caller
        }
}