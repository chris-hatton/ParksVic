package geojson.gson

import com.google.gson.*
import com.google.gson.internal.bind.TreeTypeAdapter
import com.google.gson.reflect.TypeToken
import geojson.*
import geojson.geometry.*
import geojson.geometry.impl.*
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

    private enum class Key(val string: String) {
        COORDINATES("coordinates"),
        FEATURES   ("features"),
        TYPE       ("type");

        override fun toString(): String = string
    }

    private class GeoJsonObjectDeserializer<T:GeoJsonObject>(val gson: Gson, val skipPast: TypeAdapterFactory) : JsonDeserializer<T> {

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): T {
            val typeName : String = json.asJsonObject.get(Key.TYPE.string).asString
            val geoJsonType : GeoJsonObjectType = GeoJsonObjectType.forString( typeName ) ?: throw JsonParseException("Unrecognised geometry type '$typeName'")

            fun readPosition( element: JsonElement ) : Position {
                val array = element.asJsonArray ?: throw geojson.Exception.IllegalFormat()
                if( array.size() < 2 ) throw geojson.Exception.IllegalFormat("A GeoJSON Position must have at least two elements")
                val longitude : Double  = array.get(0)?.asNumber?.toDouble() ?: throw geojson.Exception.IllegalFormat()
                val latitude  : Double  = array.get(1)?.asNumber?.toDouble() ?: throw geojson.Exception.IllegalFormat()
                val altitude  : Double? = if( array.size() >= 3 ) array.get(2).asNumber?.toDouble() else null
                return Position(longitude,latitude,altitude)
            }

            fun readListOfPositions( element: JsonElement ) : List<Position> {
                val array = element.asJsonArray ?: throw geojson.Exception.IllegalFormat()
                return array.map { readPosition(it) }
            }

            fun readListOfListOfPositions( element: JsonElement ) : List<List<Position>> {
                val array = element.asJsonArray ?: throw geojson.Exception.IllegalFormat()
                return array.map { readListOfPositions(it) }
            }

            fun readListOfListOfListOfPositions( element: JsonElement ) : List<List<List<Position>>> {
                val array = element.asJsonArray ?: throw geojson.Exception.IllegalFormat()
                return array.map { readListOfListOfPositions(it) }
            }

            fun readCoordinates( element: JsonElement ) : JsonElement {
                return (element as? JsonObject)?.get(Key.COORDINATES.string) ?: throw geojson.Exception.IllegalFormat()
            }

            @Suppress("UNCHECKED_CAST")
            return when( geoJsonType ) {

                GeoJsonObjectType.FEATURE            -> TODO()
                GeoJsonObjectType.FEATURE_COLLECTION -> TODO()

                GeoJsonObjectType.POINT              -> Point(coordinates = readPosition(readCoordinates(json))) as T
                GeoJsonObjectType.MULTI_POINT        -> MultiPoint(coordinates = readListOfPositions(readCoordinates(json))) as T
                GeoJsonObjectType.LINE_STRING        -> LineString(coordinates = readListOfPositions(readCoordinates(json))) as T
                GeoJsonObjectType.MULTI_LINE_STRING  -> MultiLineString( coordinates = readListOfListOfPositions       ( readCoordinates(json) ) ) as T
                GeoJsonObjectType.POLYGON            -> Polygon(coordinates = readListOfListOfPositions(readCoordinates(json))) as T
                GeoJsonObjectType.MULTI_POLYGON      -> MultiPolygon(coordinates = readListOfListOfListOfPositions(readCoordinates(json))) as T
            }
        }
    }

    private class GeoJsonObjectSerializer<T:GeoJsonObject>(val gson: Gson, val skipPast: TypeAdapterFactory) : JsonSerializer<T> {

        override fun serialize(src: T, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {

            fun writePosition( coordinates: Position ) : JsonArray {
                return JsonArray().apply {
                    add( coordinates.longitude )
                    add( coordinates.latitude  )
                    coordinates.altitude?.let( this::add )
                }
            }

            fun writeListOfPositions( coordinates: List<Position> ) : JsonArray {
                return JsonArray().apply { coordinates.map( ::writePosition ).forEach( this::add ) }
            }

            fun writeListOfListOfPositions( coordinates: List<List<Position>>) : JsonArray {
                return JsonArray().apply { coordinates.map( ::writeListOfPositions ).forEach( this::add ) }
            }

            fun writeListOfListOfListOfPositions( coordinates: List<List<List<Position>>> ) : JsonArray {
                return JsonArray().apply { coordinates.map( ::writeListOfListOfPositions ).forEach( this::add ) }
            }

            fun writeGeometry( coordinates: JsonArray ) : JsonObject {
                return JsonObject().apply { add( Key.COORDINATES.string, coordinates ) }
            }

            val jsonObject : JsonObject = when( src ) {

                is Feature           -> TODO()
                is FeatureCollection -> TODO()

                is Point -> writeGeometry( coordinates = writePosition                   ( src.coordinates ) )
                is MultiPoint -> writeGeometry( coordinates = writeListOfPositions            ( src.coordinates ) )
                is LineString -> writeGeometry( coordinates = writeListOfPositions            ( src.coordinates ) )
                is MultiLineString  -> writeGeometry( coordinates = writeListOfListOfPositions      ( src.coordinates ) )
                is Polygon -> writeGeometry( coordinates = writeListOfListOfPositions      ( src.coordinates ) )
                is MultiPolygon -> writeGeometry( coordinates = writeListOfListOfListOfPositions( src.coordinates ) )

                else -> throw Exception()
            }

            val geoJsonType : GeoJsonObjectType = GeoJsonObjectType.forObject(src) ?: throw geojson.Exception.UnsupportedType( src::class )
            jsonObject.addProperty( Key.TYPE.string, geoJsonType.toString())

            return jsonObject
        }
    }
}