package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.CoordinatesValidator
import org.chrishatton.geojson.Position

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.3
 */
class MultiPoint( coordinates: List<PointCoordinates> ) : Geometry<List<PointCoordinates>>( coordinates ), MultiGeometry<Point> {

    override fun split(): List<Point> = coordinates.map( ::Point )

    companion object : CoordinatesValidator<List<Position>,MultiPoint> {
        override fun validateCoordinates(coordinates: List<Position>) {
            //TODO: Implement some rules, any?
        }
    }
}