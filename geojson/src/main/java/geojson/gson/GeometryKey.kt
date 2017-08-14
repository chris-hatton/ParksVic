package geojson.gson

import geojson.geometry.*
import kotlin.reflect.KClass

/**
 * Codifies a mapping between GeoJSON types and their Kotlin-class representations.
 * Used by [GeometrySerializer] and [GeometryDeserializer].
 */
enum class GeometryKey(private val string: String, val `class`: KClass<*>) {
    POINT             ("Point"           , Point          ::class ),
    MULTI_POINT       ("MultiPoint"      , MultiPoint     ::class ),
    LINE_STRING       ("LineString"      , LineString     ::class ),
    MULTI_LINE_STRING ("MultiLineString" , MultiLineString::class ),
    POLYGON           ("Polygon"         , Polygon        ::class ),
    MULTI_POLYGON     ("MultiPolygon"    , MultiPolygon   ::class );

    override fun toString(): String = string

    companion object {
        val key : String = "type"

        fun forString( keyString: String ) : GeometryKey? {
            return GeometryKey.values().find { it.string == keyString }
        }

        fun forGeometry( geometry: Geometry<*> ) : GeometryKey? {
            return GeometryKey.values().find { it::class == geometry::class }
        }
    }
}