package org.chrishatton.geojson

import org.chrishatton.geojson.geometry.Point


data class BoundingBox(
        val southWest : Point,
        val northEast : Point
) {
    override fun toString(): String {
        // left,lower,right,upper,crs
        return "${southWest.longitude},${southWest.latitude},${northEast.longitude},${northEast.latitude},EPSG:4326"
    }
}