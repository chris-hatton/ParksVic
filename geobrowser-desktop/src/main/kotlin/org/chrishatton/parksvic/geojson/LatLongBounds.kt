package org.chrishatton.parksvic.geojson

import com.lynden.gmapsfx.javascript.`object`.LatLongBounds
import org.chrishatton.geojson.BoundingBox
import org.chrishatton.geojson.geometry.Point

fun LatLongBounds.toBoundingBoxes() : Array<BoundingBox> {

    return if( northEast.longitude < southWest.longitude ) {
        arrayOf(
                BoundingBox(Point(-180.0, southWest.latitude), northEast.toPoint()),
                BoundingBox(southWest.toPoint(), Point(180.0, northEast.latitude))
        )
    } else {
        arrayOf( BoundingBox( southWest = southWest.toPoint(), northEast = northEast.toPoint() ) )
    }
}
