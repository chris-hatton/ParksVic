package geojson.gson

import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import geojson.geometry.MultiPoint
import geojson.geometry.*
import java.lang.reflect.Type
import kotlin.jvm.java
import kotlin.reflect.KClass
import kotlin.reflect.KType


/**
 * Created by Chris on 30/07/2017.
 */
class GeometryDeserializer : JsonDeserializer<Geometry<*>> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Geometry<*> {

        val jobject = json.asJsonObject

        val typeName : String = jobject.get("type").asString

        val geometryType : KClass<*> = GeometryKey.forString( typeName )?.`class` ?: throw JsonParseException("Unrecognised geometry type '$type'")

        return context.deserialize(json,geometryType.java)
    }
}