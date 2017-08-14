package geojson.geometry

import geojson.Exception
import geojson.Position

typealias LineStringCoordinates = List<Position>

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.4
 */
class LineString( coordinates: LineStringCoordinates ) : Geometry<LineStringCoordinates>( coordinates ) {

    companion object : Geometry.Companion<LineString,LineStringCoordinates> {

        override fun validateCoordinates(coordinates: List<Position>) {
            if( coordinates.count() < 2 ) {
                throw Exception.IllegalFormat("A LineString must have at least two positions.")
            }
        }
    }
}