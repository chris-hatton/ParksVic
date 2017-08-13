package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.Position

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.3
 */
class MultiPoint( coordinates: List<Position> ) : Geometry<List<Position>>( coordinates ), MultiGeometry<Point> {
    override fun split(): List<Point> = coordinates.map( ::Point )
}