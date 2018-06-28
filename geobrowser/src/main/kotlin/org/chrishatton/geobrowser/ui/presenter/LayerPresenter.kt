package org.chrishatton.geobrowser.ui.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import opengis.model.app.MapViewLayer
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.Nullable
import org.chrishatton.geobrowser.ui.contract.LayerViewContract

class LayerPresenter(val layer: MapViewLayer, val viewStream: Observable<Nullable<LayerViewContract>> ) : Presenter<LayerViewContract>(viewStream) {

    lateinit var isVisible : Observable<Boolean>

    override fun onCreate(subscriptions: CompositeDisposable) {
        super.onCreate(subscriptions)

        isVisible = viewStream.switchMap { (view) ->
            view?.isSelectedStream ?: Observable.never<Boolean>()
        }
        .startWith(false)
        .replay(1)
    }

    override fun onViewAttached(view: LayerViewContract, viewSubscriptions: CompositeDisposable) {
        super.onViewAttached(view, viewSubscriptions)

        val title = when(layer) {
            is MapViewLayer.Tile.Wms  -> layer.layer.name
            is MapViewLayer.Tile.Wmts -> layer.layer.name
            is MapViewLayer.Feature   -> layer.featureType.name
            else                      -> null
        } ?: ""

        view.title.accept(title)
    }

    override fun onViewDetached(view: LayerViewContract) {
        super.onViewDetached(view)


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}