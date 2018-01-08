package org.chrishatton.geobrowser.geojson

import com.google.android.gms.maps.model.LatLng
import geojson.geometry.impl.Point

fun LatLng.toPoint() : Point {
    return Point(longitude, latitude)
}
