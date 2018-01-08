package org.chrishatton.parksvic.data.adapter

import geojson.Feature

interface FeatureAdapter<out T> {
    fun convert( feature: Feature) : T
}
