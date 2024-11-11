package com.movielist.data

import com.google.firebase.Timestamp
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.Calendar

class FirebaseTimestampAdapter {

    /*
    * Data fra Firestore (Timestamp) blir til Calendar
    */

    @ToJson
    fun calendarToTimestamp(calendar: Calendar): Timestamp {
        val seconds = calendar.timeInMillis / 1000  // Sekunder
        val nanos = (calendar.timeInMillis % 1000) * 1000000  // Nanosekunder
        return Timestamp(seconds, nanos.toInt())  // Lager en Firestore Timestamp
    }

    //
    @FromJson
    fun timestampToCalendar(timestamp: Timestamp): Calendar {
        val calendar = Calendar.getInstance()
        val timeInMillis = timestamp.seconds * 1000L + timestamp.nanoseconds / 1000000L  // Rekonstruer millisekunder
        calendar.timeInMillis = timeInMillis  // Sett tilbake tid i Calendar
        return calendar
    }

    // For serialisering av Firebase Timestamp til JSON
    @ToJson
    fun timestampToJson(timestamp: Timestamp): String {
        return "${timestamp.seconds}.${timestamp.nanoseconds}"
    }

    // For deserialisering av JSON til Firebase Timestamp
    // Vi går ikke fra JSON til Firebase akkurat nå -> Det lar vi nok Firebase ta seg av selv?
    @FromJson
    fun jsonToTimestamp(json: String): Timestamp {
        val parts = json.split(".")
        val seconds = parts[0].toLong()
        val nanos = if (parts.size > 1) parts[1].toInt() else 0
        return Timestamp(seconds, nanos)
    }
}