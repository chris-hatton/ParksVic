package org.chrishatton.geobrowser.data.model

import geojson.geometry.impl.Point

data class Circle(
        val center: Point,
        val radius: Double
    )