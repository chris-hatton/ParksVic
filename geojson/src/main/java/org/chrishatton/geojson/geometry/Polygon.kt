package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.CoordinatesValidator
import org.chrishatton.geojson.Exception
import org.chrishatton.geojson.LinearRing
import org.chrishatton.geojson.Position

typealias PolygonCoordinates = List<List<Position>>

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.6
 */
class Polygon( coordinates: PolygonCoordinates ) : Geometry<PolygonCoordinates>( coordinates ) {

    companion object : CoordinatesValidator<PolygonCoordinates,Polygon> {
        override fun validateCoordinates(coordinates: PolygonCoordinates) {

            coordinates.withIndex().find { (_,positions) -> !LinearRing.isLinearRing(positions) }?.let {
                (index,positions) ->
                throw Exception.IllegalFormat("The top-level element at index [$index] of this Polygon's coordinates does not meet Linear Ring criteria, they are: '$positions'")
            }

            // TODO: Check that first Linear Ring is outer, encompassing all subsequent rings.
        }
    }
}

