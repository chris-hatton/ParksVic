package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.GeoJsonObject

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1
 */
abstract class Geometry<out C>(val coordinates: C ) : GeoJsonObject()