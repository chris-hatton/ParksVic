package org.chrishatton.geojson

import org.chrishatton.geojson.geometry.Point

data class Feature(
        val id         : String,
        val geometry   : Point,
        val properties : Map<String,Any>
    )