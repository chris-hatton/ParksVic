package org.chrishatton.geobrowser.ui.contract

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import opengis.model.app.MapViewLayer
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.geobrowser.ui.presenter.LayerPresenter

/**
 * Created by Chris on 19/01/2018.
 */
interface LayersViewContract : ViewContract {

    var layerPresentersConsumer : Consumer<Iterable<LayerPresenter>>

    var layerViewBindingsStream : Observable<Map<MapViewLayer,LayerViewContract>>
}