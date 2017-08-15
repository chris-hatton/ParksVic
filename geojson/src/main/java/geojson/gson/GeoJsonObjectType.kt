package geojson.gson

import geojson.Feature
import geojson.FeatureCollection
import geojson.GeoJsonObject
import geojson.geometry.*
import kotlin.reflect.KClass

/**
 * Codifies a mapping between GeoJSON types and their Kotlin-class representations.
 * Used by [GeometrySerializer] and [GeometryDeserializer].
 */
enum class GeoJsonObjectType(private val string: String, val `class`: KClass<*>) {

    // Features
    FEATURE           ("Feature"          , Feature           ::class ),
    FEATURE_COLLECTION("FeatureCollection", FeatureCollection ::class ),

    // Geometries
    POINT             ("Point"            , Point             ::class ),
    MULTI_POINT       ("MultiPoint"       , MultiPoint        ::class ),
    LINE_STRING       ("LineString"       , LineString        ::class ),
    MULTI_LINE_STRING ("MultiLineString"  , MultiLineString   ::class ),
    POLYGON           ("Polygon"          , Polygon           ::class ),
    MULTI_POLYGON     ("MultiPolygon"     , MultiPolygon      ::class );

    override fun toString(): String = string

    companion object {

        val key : String = "type"

        fun forString( type: String ) : GeoJsonObjectType? {
            return GeoJsonObjectType.values().find { it.string.equals( other = type, ignoreCase = true) }
        }

        fun forObject(`object`: GeoJsonObject ) : GeoJsonObjectType? {
            return GeoJsonObjectType.values().find { it::class == `object`::class }
        }
    }
}