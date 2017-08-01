package org.chrishatton.geojson


data class FeatureCollection(
        val totalFeatures : Int,
        val features      : List<Feature>,
        val crs           : Any?
)
