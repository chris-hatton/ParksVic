package org.chrishatton.geojson.gson

import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import org.chrishatton.geojson.geometry.*
import java.lang.reflect.Type
import kotlin.jvm.java


/**
 * Created by Chris on 30/07/2017.
 */
class GeometryDeserializer : JsonDeserializer<Geometry<*>> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Geometry<*> {

        val jobject = json.asJsonObject

        val typeName : String = jobject.get("type").asString

        val type : Type = when(typeName) {
            "Point"           -> Point           ::class.java
            "MultiPoint"      -> MultiPoint      ::class.java
            "LineString"      -> LineString      ::class.java
            "MultiLineString" -> MultiLineString ::class.java
            "Polygon"         -> Polygon         ::class.java
            "MultiPolygon"    -> MultiPolygon    ::class.java

            else -> throw JsonParseException("Unrecognised geometry type '$type'")
        }

        return context.deserialize(json,type)
    }
}