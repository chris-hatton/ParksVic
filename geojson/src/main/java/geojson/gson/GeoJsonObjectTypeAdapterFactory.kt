package geojson.gson

import com.google.gson.*
import com.google.gson.internal.bind.TreeTypeAdapter
import com.google.gson.reflect.TypeToken
import geojson.Feature
import geojson.FeatureCollection
import geojson.GeoJsonObject
import geojson.Position
import geojson.geometry.*
import java.lang.reflect.Type

class GeoJsonObjectTypeAdapterFactory : TypeAdapterFactory {

    companion object {
        val geoJsonObjectTypeToken: TypeToken<GeoJsonObject> = TypeToken.get(GeoJsonObject::class.java)
    }

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        return if( Geometry::class.java.isAssignableFrom(type.rawType) ) {

            val serializer   : JsonSerializer  <GeoJsonObject> = GeoJsonObjectSerializer  (gson,this)
            val deserializer : JsonDeserializer<GeoJsonObject> = GeoJsonObjectDeserializer(gson,this)

            // As per Section 1.2 of RFC7946, the order of GeoJSON elements is not guaranteed
            // Therefore we are bound to use `TreeTypeAdapter` which pre-parses and allows random
            // to a JSON structure.
            // See: https://tools.ietf.org/html/rfc7946#section-1.2
            @Suppress("UNCHECKED_CAST")
            TreeTypeAdapter<GeoJsonObject>(serializer,deserializer,gson, geoJsonObjectTypeToken,this) as TypeAdapter<T>
        } else null
    }

    private class GeoJsonObjectDeserializer<T:GeoJsonObject>(val gson: Gson, val skipPast: TypeAdapterFactory) : JsonDeserializer<T> {

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): T {
            val typeName : String = json.asJsonObject.get(GeoJsonObjectType.key).asString
            val geoJsonType : GeoJsonObjectType = GeoJsonObjectType.forString( typeName ) ?: throw JsonParseException("Unrecognised geometry type '$typeName'")

            fun readPosition() : Position {
                TODO()
            }

            fun readListOfPositions() : List<Position> {
                TODO()
            }

            fun readListOfListOfPositions() : List<List<Position>> {
                TODO()
            }

            fun readListOfListOfListOfPositions() : List<List<List<Position>>> {
                TODO()
            }

            @Suppress("UNCHECKED_CAST")
            return when( geoJsonType ) {

                GeoJsonObjectType.FEATURE            -> TODO()
                GeoJsonObjectType.FEATURE_COLLECTION -> TODO()

                GeoJsonObjectType.POINT              -> Point          ( coordinates = readPosition()                    ) as T
                GeoJsonObjectType.MULTI_POINT        -> MultiPoint     ( coordinates = readListOfPositions()             ) as T
                GeoJsonObjectType.LINE_STRING        -> LineString     ( coordinates = readListOfPositions()             ) as T
                GeoJsonObjectType.MULTI_LINE_STRING  -> MultiLineString( coordinates = readListOfListOfPositions()       ) as T
                GeoJsonObjectType.POLYGON            -> Polygon        ( coordinates = readListOfListOfPositions()       ) as T
                GeoJsonObjectType.MULTI_POLYGON      -> MultiPolygon   ( coordinates = readListOfListOfListOfPositions() ) as T
            }
        }
    }

    private class GeoJsonObjectSerializer<T:GeoJsonObject>(val gson: Gson, val skipPast: TypeAdapterFactory) : JsonSerializer<T> {

        override fun serialize(src: T, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {

            fun writePosition( coordinates: Position ) {
                TODO()
            }

            fun writeListOfPositions( coordinates: List<Position> ) {
                TODO()
            }

            fun writeListOfListOfPositions( coordinates: List<List<Position>>) {
                TODO()
            }

            fun writeListOfListOfListOfPositions( coordinates: List<List<List<Position>>> ) {
                TODO()
            }

            val geoJsonType : GeoJsonObjectType = GeoJsonObjectType.values().find { it.`class` == src::class } ?: throw Exception()

            when( src ) {

                is Feature           -> TODO()
                is FeatureCollection -> TODO()

                is Point            -> writePosition                   ( src.coordinates )
                is MultiPoint       -> writeListOfPositions            ( src.coordinates )
                is LineString       -> writeListOfPositions            ( src.coordinates )
                is MultiLineString  -> writeListOfListOfPositions      ( src.coordinates )
                is Polygon          -> writeListOfListOfPositions      ( src.coordinates )
                is MultiPolygon     -> writeListOfListOfListOfPositions( src.coordinates )
            }

            val typeToken = TypeToken.get(src::class.java)
            @Suppress("UNCHECKED_CAST")
            val delegateAdapter : TypeAdapter<T> = gson.getDelegateAdapter(skipPast,typeToken) as TypeAdapter<T>

            val jsonObject = delegateAdapter.toJsonTree(src) as JsonObject
            jsonObject.addProperty( "type", geoJsonType.name )

            return jsonObject
        }
    }
}