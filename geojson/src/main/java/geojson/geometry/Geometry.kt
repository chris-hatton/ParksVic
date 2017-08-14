package geojson.geometry

import geojson.GeoJsonObject

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1
 */
abstract class Geometry<out C>(val coordinates: C ) : GeoJsonObject() {

    interface Companion<G:Geometry<C>,C> {

        /**
         * If the coordinates are valid for Geometry G, this funcion has no effect.
         * If they are invalid, an exception will be thrown.
         */
        fun validateCoordinates( coordinates: C )
    }
}