package org.chrishatton.parksvic.geojson

import com.lynden.gmapsfx.javascript.`object`.LatLong
import org.chrishatton.geojson.geometry.Point

fun LatLong.toPoint() : Point {
    return Point( longitude, latitude )
}
