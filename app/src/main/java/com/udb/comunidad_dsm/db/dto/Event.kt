package com.udb.comunidad_dsm.db.dto

import java.time.LocalDate
import java.time.LocalTime

data class Event (
    val date: String = "",
    val location: String = "",
    val description: String = ""
)

data class EventForm (
    var localDate: LocalDate,
    var localTime: LocalTime,
    var location: String = "",
    var description: String = ""
)