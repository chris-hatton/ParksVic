package geojson.gson

import geojson.Exception
import geojson.Feature
import geojson.FeatureCollection
import geojson.GeoJsonObject
import geojson.geometry.*
import geojson.geometry.impl.*
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
    POINT             ("Point"            , Point::class ),
    MULTI_POINT       ("MultiPoint"       , MultiPoint::class ),
    LINE_STRING       ("LineString"       , LineString::class ),
    MULTI_LINE_STRING ("MultiLineString"  , MultiLineString   ::class ),
    POLYGON           ("Polygon"          , Polygon::class ),
    MULTI_POLYGON     ("MultiPolygon"     , MultiPolygon::class );

    override fun toString(): String = string

    companion object {

        fun forString( typeName: String ) : GeoJsonObjectType {
            val type : GeoJsonObjectType? = GeoJsonObjectType.values().find { it.string.equals( other = typeName, ignoreCase = true) }
            return type ?: throw Exception.UnknownTypeName( typeName )
        }

        fun forObject(`object`: GeoJsonObject ) : GeoJsonObjectType {
            val type : GeoJsonObjectType? =  GeoJsonObjectType.values().find { it.`class` == `object`::class }
            return type ?: throw Exception.UnsupportedType( `object`::class )
        }
    }
}