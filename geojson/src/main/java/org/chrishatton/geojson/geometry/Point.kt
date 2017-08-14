package org.chrishatton.geojson.geometry

import org.chrishatton.geojson.Position

typealias PointCoordinates = Position

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.2
 */
class Point( coordinates: PointCoordinates ) : Geometry<PointCoordinates>( coordinates ) {

    /**
     * Convenience constructor, forming the mandated 'single Position' coordinate from Latitude
     * and Longitude components.
     */
    constructor( longitude : Double, latitude: Double) : this( Position( longitude = longitude, latitude = latitude ) )

    /** Convenience accessor for the Longitude component of this [Point]s coordinate. */
    val longitude : Double = coordinates.longitude

    /** Convenience accessor for the Latitude component of this [Point]s coordinate. */
    val latitude  : Double = coordinates.latitude

    override fun toString(): String = "POINT(${coordinates.latitude}%20$${coordinates.longitude})"
}