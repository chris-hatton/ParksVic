package org.chrishatton.geojson.geometry

/**
 * GeoJSON (RFC7946) specifies several 'Multi-' geometry types whose coordinates are a direct
 * grouping of the coordinates of other types; these being:
 *
 * MultiPoint      -> Point
 * MultiLineString -> LineString
 * MultiPolygon    -> Polygon
 *
 * For convenience sake; this library identifies these types with the MultiGeometry interface,
 * which guarantees the provision of a function to split out a collection of base types from the
 * Multi type.
 */
interface MultiGeometry<out G> {
    fun split() : List<G>
}