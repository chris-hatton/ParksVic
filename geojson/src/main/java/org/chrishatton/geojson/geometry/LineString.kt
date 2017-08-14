package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.CoordinatesValidator
import org.chrishatton.geojson.Exception
import org.chrishatton.geojson.Position

typealias LineStringCoordinates = List<Position>

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.4
 */
class LineString( coordinates: LineStringCoordinates ) : Geometry<LineStringCoordinates>( coordinates ) {

    companion object : CoordinatesValidator<LineStringCoordinates,LineString> {
        override fun validateCoordinates(coordinates: List<Position>) {
            if( coordinates.count() < 2 ) {
                throw Exception.IllegalFormat("A LineString must have at least two positions.")
            }
        }
    }
}