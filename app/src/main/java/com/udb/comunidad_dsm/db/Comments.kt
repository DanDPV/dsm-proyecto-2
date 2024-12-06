package com.udb.comunidad_dsm.db

import com.google.firebase.firestore.FirebaseFirestore
import com.udb.comunidad_dsm.db.dto.Comment

val COMMENTS_COLLECTION = "events-comments"

fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection(COMMENTS_COLLECTION)
        .add(comment)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
}

fun getComments(eventId: String, onSuccess: (List<Comment>) -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection(COMMENTS_COLLECTION)
        .whereEqualTo("eventId", eventId)
        .get()
        .addOnSuccessListener { result ->
            val comments = result.documents.mapNotNull { it.toObject(Comment::class.java) }
            onSuccess(comments)
        }
        .addOnFailureListener { e -> onFailure(e) }
}