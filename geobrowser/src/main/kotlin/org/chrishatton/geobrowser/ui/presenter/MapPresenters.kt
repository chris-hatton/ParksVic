package org.chrishatton.geobrowser.ui.presenter

import io.reactivex.functions.Consumer
import opengis.model.app.MapViewLayer

/**
 * Bridge the 'mapLayers' property of MapPresenter, to an Rx Consumer.
 */
val MapPresenter.mapLayersConsumer : Consumer<Iterable<MapViewLayer>>
    get() = Consumer { mapViewLayers ->
        this.mapLayers = mapViewLayers.toList()
    }