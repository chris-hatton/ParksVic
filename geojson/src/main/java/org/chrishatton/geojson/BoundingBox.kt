package org.chrishatton.geojson

import org.chrishatton.geojson.geometry.Point

/**
 * https://tools.ietf.org/html/rfc7946#section-5
 */
data class BoundingBox(
        val southWest : Point,
        val northEast : Point
) {
    override fun toString(): String {
        // left,lower,right,upper,crs
        return "${southWest.longitude},${southWest.latitude},${northEast.longitude},${northEast.latitude}"
    }

    companion object {
        var all : BoundingBox = BoundingBox( southWest = Point(-180.0,-90.0), northEast = Point(180.0,90.0) )
    }
}