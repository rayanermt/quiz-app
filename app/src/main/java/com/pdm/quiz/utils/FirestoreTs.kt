package com.pdm.quiz.utils

import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.getMillis(field: String): Long {
    val ts = this.getTimestamp(field)
    if (ts != null) return ts.toDate().time

    val raw = this.getLong(field)
    if (raw != null) {
        return if (raw < 10_000_000_000L) raw * 1000 else raw
    }
    return 0L
}
