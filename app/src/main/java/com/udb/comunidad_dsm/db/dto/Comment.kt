package com.udb.comunidad_dsm.db.dto

data class Comment(
    val id: String? = null,
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)