package org.chrishatton.geojson.geometry

data class Point(
    val longitude : Double,
    val latitude  : Double
) {
    override fun toString(): String = "POINT($latitude%20$longitude)"
}