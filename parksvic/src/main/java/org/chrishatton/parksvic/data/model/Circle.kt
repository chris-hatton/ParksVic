package org.chrishatton.parksvic.data.model

import geojson.geometry.Point

data class Circle(
        val center: org.chrishatton.geojson.geometry.Point,
        val radius: Double
)