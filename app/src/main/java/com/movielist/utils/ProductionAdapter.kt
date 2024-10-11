package com.movielist.utils

import com.movielist.data.Episode
import com.movielist.data.Movie
import com.movielist.data.Production
import com.movielist.data.TVShow
import com.squareup.moshi.*

class ProductionAdapter : JsonAdapter<Production>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Production? {
        var type: String? = null

        // Start deserialisering av JSON
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "type" -> type = reader.nextString() // Hent typefeltet for å bestemme hvilken type produksjon vi skal deserialisere til
                else -> reader.skipValue() // Hvis vi ikke er interessert i andre felt, hopp over dem
            }
        }
        reader.endObject()

        // Avhengig av verdien av `type`, deserialiser til riktig klasse
        return when (type) {
            "Movie" -> Moshi.Builder().build().adapter(Movie::class.java).fromJson(reader)
            "TVShow" -> Moshi.Builder().build().adapter(TVShow::class.java).fromJson(reader)
            "Episode" -> Moshi.Builder().build().adapter(Episode::class.java).fromJson(reader)
            else -> throw JsonDataException("Unknown production type: $type") // Hvis type er ukjent, kast en feil
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Production?) {
        // Denne metoden kan brukes for å skrive ut produksjonsobjektet som JSON
    }
}
