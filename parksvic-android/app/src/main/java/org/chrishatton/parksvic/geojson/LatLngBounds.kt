package org.chrishatton.parksvic.geojson

import com.google.android.gms.maps.model.LatLngBounds
import geojson.BoundingBox
import geojson.geometry.Point


fun LatLngBounds.toBoundingBoxes() : Array<BoundingBox> {

    return if( northeast.longitude < southwest.longitude ) {
        arrayOf(
                BoundingBox(Point(-180.0, southwest.latitude), northeast.toPoint()),
                BoundingBox(southwest.toPoint(), Point(180.0, northeast.latitude))
        )
    } else {
        arrayOf( BoundingBox( southWest = southwest.toPoint(), northEast = northeast.toPoint() ) )
    }
}