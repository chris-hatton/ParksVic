package geojson.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import geojson.Position

/**
 * GSON type-adapter to convert [Position] objects to & from the numeric pair/triplet representation
 * specified in RFC7946, see: https://tools.ietf.org/html/rfc7946#section-3.1.1
 */
class PositionTypeAdapter : TypeAdapter<Position>() {

    override fun write(out: JsonWriter?, value: Position?) {
        if( out == null ) { return }

        if( value == null ) {
            out.nullValue()
        } else {
            out.beginArray()
            out.value(value.longitude)
            out.value(value.latitude)
            value.altitude?.let{ out.value(it) }
            out.endArray()
        }
    }

    override fun read(`in`: JsonReader?): Position {
        if( `in` == null ) { throw Exception() }

        `in`.beginArray()
        val longitude : Double = `in`.nextDouble()
        val latitude  : Double = `in`.nextDouble()
        val altitude  : Double? = if( `in`.peek() == JsonToken.NUMBER ) {
            `in`.nextDouble()
        } else {
            null
        }
        `in`.endArray()

        return Position( longitude, latitude, altitude )
    }
}