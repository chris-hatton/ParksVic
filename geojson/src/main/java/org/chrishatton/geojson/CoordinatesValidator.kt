package org.chrishatton.geojson

import org.chrishatton.geojson.geometry.Geometry

/**
 * By convention of this library implementation; the 'companion object' of each Geometry class
 * implements this interface, checking that the coordinates of the object-instance comply
 * with the requirements described in the RFC7946 specification.
 */
interface CoordinatesValidator<C,G: Geometry<C>> {

    /**
     *
     * If the coordinates are valid for Geometry G, this funcion has no effect.
     * If they are invalid, an exception will be thrown.
     */
    fun validateCoordinates( coordinates: C )
}