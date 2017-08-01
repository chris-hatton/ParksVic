package org.chrishatton.parksvic.geojson

import com.google.android.gms.maps.model.LatLng
import org.chrishatton.geojson.geometry.Point

fun LatLng.toPoint() : Point {
    return Point( longitude, latitude )
}
