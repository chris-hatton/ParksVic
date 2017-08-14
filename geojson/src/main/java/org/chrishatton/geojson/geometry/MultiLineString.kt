package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.CoordinatesValidator
import org.chrishatton.geojson.Position

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.5
 */
class MultiLineString( coordinates: List<LineStringCoordinates> ) : Geometry<List<LineStringCoordinates>>( coordinates ), MultiGeometry<LineString> {

    companion object : CoordinatesValidator<List<LineStringCoordinates>,MultiLineString> {

        /** Each child component of the [MultiLineString] must be [LineString] compliant. */
        override fun validateCoordinates(coordinates: List<List<Position>>) {
            coordinates.forEach( LineString.Companion::validateCoordinates )
        }
    }

    override fun split(): List<LineString> = coordinates.map( ::LineString )
}