package org.chrishatton.geobrowser.ui.contract

import geojson.Feature
import opengis.process.TileProvider
import org.chrishatton.crosswind.ui.contract.ViewContract

/**
 * Created by Chris on 19/01/2018.
 */
interface MapViewContract : ViewContract {
    fun addTileLayer( provider: TileProvider<*>)
    fun removeTileLayer( provider: TileProvider<*>) : Boolean
    fun addFeature( feature: Feature )
    fun removeFeature( feature: Feature ) : Boolean
}