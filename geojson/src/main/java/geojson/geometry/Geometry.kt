package geojson.geometry

import geojson.GeoJsonObject
import geojson.Position

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1
 */
abstract class Geometry<out C>(val coordinates: C ) : GeoJsonObject() {

    interface Companion<out G: Geometry<C>,C> {

        /**
         * If the coordinates are valid for Geometry G, this function has no effect.
         * If they are invalid, an exception will be thrown.
         */
        fun validateCoordinates( coordinates: C )

        fun fromVertices( vertexPositions: List<Position> ) : G

        fun fromCoordinates( coordinates: C ) : G
    }

//    internal interface InternalCompanion<out G:Geometry<C>,C> {
//        fun fromUnsafeCoordinates( coordinates: C ) : G
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Geometry<*>

        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates?.hashCode() ?: 0
    }
}