package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.CoordinatesValidator
import org.chrishatton.geojson.Exception
import org.chrishatton.geojson.Position

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.4
 */
class LineString( coordinates: List<Position> ) : Geometry<List<Position>>( coordinates ) {

    companion object : CoordinatesValidator<List<Position>,LineString> {
        override fun validateCoordinates(coordinates: List<Position>) {
            if( coordinates.count() < 2 ) {
                throw Exception.IllegalFormat("A LineString must have at least two positions.")
            }
        }
    }
}