package geojson.geometry.impl

import geojson.Exception
import geojson.LinearRing
import geojson.Position
import geojson.geometry.Geometry

typealias PolygonCoordinates = List<List<Position>>

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.6
 */
class Polygon internal constructor( coordinates: PolygonCoordinates) : Geometry<PolygonCoordinates>( coordinates ) {

    companion object : Geometry.Companion<Polygon, PolygonCoordinates> {

        override fun fromCoordinates(coordinates: PolygonCoordinates): Polygon = Polygon(coordinates)

        fun fromVertexPairs( vararg vertexPairs: Pair<Double,Double> ) : Polygon {
            val vertices : List<Position> = vertexPairs.map { Position( it.first, it.second ) }
            return fromVertices(vertices)
        }

        override fun fromVertices( vertexPositions: List<Position> ) : Polygon {
            return Polygon(coordinates = listOf(vertexPositions + vertexPositions.first()))
        }

        override fun validateCoordinates(coordinates: PolygonCoordinates) {

            coordinates.withIndex().find { (_,positions) -> !LinearRing.isLinearRing(positions) }?.let {
                (index,positions) ->
                throw Exception.IllegalFormat("The top-level element at index [$index] of this Polygon's coordinates does not meet Linear Ring criteria, they are: '$positions'")
            }

            // TODO: Check that first Linear Ring is outer, encompassing all subsequent rings.
        }
    }
}

