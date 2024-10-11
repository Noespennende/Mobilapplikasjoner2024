package com.movielist.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.UUID

class UUIDAdapter {

    @ToJson
    fun toJson(uuid: UUID): String {
        return uuid.toString()  // Konverterer UUID til en String
    }

    @FromJson
    fun fromJson(uuidString: String): UUID {
        return UUID.fromString(uuidString)  // Konverterer en String tilbake til UUID
    }
}