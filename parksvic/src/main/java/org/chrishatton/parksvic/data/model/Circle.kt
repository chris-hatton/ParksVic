package org.chrishatton.parksvic.data.model

data class Circle(
        val center: org.chrishatton.geojson.geometry.Point,
        val radius: Double
)