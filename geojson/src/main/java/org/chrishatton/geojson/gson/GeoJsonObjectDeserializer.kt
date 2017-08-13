package org.chrishatton.geojson.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.chrishatton.geojson.Feature
import org.chrishatton.geojson.FeatureCollection
import org.chrishatton.geojson.geometry.*
import java.lang.reflect.Type


class GeoJsonObjectDeserializer : JsonDeserializer<Geometry<*>> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Geometry<*> {

        val jobject = json.asJsonObject

        val typeName : String = jobject.get("type").asString

        val type : Type = when(typeName) {
            "Feature"           -> Feature::class.java
            "FeatureCollection" -> FeatureCollection::class.java

            else -> throw JsonParseException("Unrecognised GeoJSON object type '$type'")
        }

        return context.deserialize(json,type)
    }
}