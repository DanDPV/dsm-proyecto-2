package com.udb.comunidad_dsm

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun mergeDateAndTimeToIso(date: LocalDate, time: LocalTime): String {
    // Combine LocalDate and LocalTime into LocalDateTime
    val localDateTime = LocalDateTime.of(date, time)
    // Return the ISO 8601 string representation
    return localDateTime.toString()
}

fun parseIsoStringToDateAndTime(isoString: String): Pair<LocalDate, LocalTime> {
    // Parse the ISO string into a LocalDateTime
    val localDateTime = LocalDateTime.parse(isoString)

    // Extract LocalDate and LocalTime
    val localDate = localDateTime.toLocalDate()
    val localTime = localDateTime.toLocalTime()

    return Pair(localDate, localTime)
}