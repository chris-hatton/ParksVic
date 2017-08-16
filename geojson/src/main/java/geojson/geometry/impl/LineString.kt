package geojson.geometry.impl

import geojson.Exception
import geojson.Position
import geojson.geometry.Geometry

typealias LineStringCoordinates = List<Position>

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.4
 */
class LineString( coordinates: LineStringCoordinates) : Geometry<LineStringCoordinates>( coordinates ) {

    companion object : Geometry.Companion<LineString, LineStringCoordinates> {

        override fun fromCoordinates(coordinates: LineStringCoordinates): LineString = LineString(coordinates)

        override fun validateCoordinates(coordinates: List<Position>) {
            if( coordinates.count() < 2 ) {
                throw Exception.IllegalFormat("A LineString must have at least two positions.")
            }
        }

        fun fromVertexPairs( vararg vertexPairs: Pair<Double,Double> ) : LineString {
            val vertices : List<Position> = vertexPairs.map { Position( it.first, it.second ) }
            return fromVertices(vertices)
        }

        override fun fromVertices( vertexPositions: List<Position> ) : LineString {
            return LineString(coordinates = vertexPositions)
        }
    }
}