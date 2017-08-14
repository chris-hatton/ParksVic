package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.CoordinatesValidator
import org.chrishatton.geojson.Position

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.7
 */
class MultiPolygon( coordinates: List<PolygonCoordinates> ) : Geometry<List<PolygonCoordinates>>( coordinates ), MultiGeometry<Polygon> {

    companion object : CoordinatesValidator<List<PolygonCoordinates>,MultiPolygon> {
        override fun validateCoordinates(coordinates: List<PolygonCoordinates>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun split(): List<Polygon> = coordinates.map( ::Polygon )

}