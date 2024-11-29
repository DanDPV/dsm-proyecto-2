package com.udb.comunidad_dsm.db

import android.util.Log
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.udb.comunidad_dsm.db.dto.Event
import com.udb.comunidad_dsm.db.dto.EventConfirmation
import com.udb.comunidad_dsm.db.dto.EventForm
import com.udb.comunidad_dsm.parseIsoStringToDateAndTime
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

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

fun getCloserEvents(onSuccess: (List<Event>) -> Unit) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = Date()
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection(EVENTS_COLLECTION)
        .where(Filter.greaterThan("date", dateFormat.format(currentDate)))
        .orderBy("date", Query.Direction.ASCENDING)
        .limit(5)
        .get()
        .addOnSuccessListener { results ->
            val events = results.documents.mapNotNull { document ->
                val event = document.toObject(Event::class.java)
                event
            }
            onSuccess(events)
        }
}

fun confirmParticipation(eventId: String, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    val refs = firestore.collection(EVENTS_CONFIRMATIONS)
    val query = refs.where(Filter.and(
        Filter.equalTo("eventId", eventId),
        Filter.equalTo("userId", userId)
    ))
    query.get()
        .addOnSuccessListener {results ->
            if(results.documents.isEmpty()) {
                val eventConfirmation = EventConfirmation(null, userId, eventId)
                firestore.collection(EVENTS_CONFIRMATIONS)
                    .add(eventConfirmation)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e) }
            } else {
                onSuccess()
            }
        }
}