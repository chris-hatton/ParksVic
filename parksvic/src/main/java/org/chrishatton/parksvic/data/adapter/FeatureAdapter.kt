package org.chrishatton.parksvic.data.adapter

import org.chrishatton.geojson.Feature

interface FeatureAdapter<out T> {
    fun convert( feature: Feature) : T
}
