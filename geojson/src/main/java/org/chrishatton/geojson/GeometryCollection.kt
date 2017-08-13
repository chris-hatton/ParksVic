package org.chrishatton.geojson

import org.chrishatton.geojson.geometry.Geometry

/**
 *
 */
class GeometryCollection(val geometries: List<Geometry<*>>) : GeoJsonObject() {

}