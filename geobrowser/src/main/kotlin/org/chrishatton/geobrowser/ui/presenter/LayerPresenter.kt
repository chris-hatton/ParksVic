package org.chrishatton.geobrowser.ui.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import opengis.model.app.MapViewLayer
import org.chrishatton.crosswind.rx.*
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.Optional
import org.chrishatton.geobrowser.ui.contract.LayerViewContract

class LayerPresenter(val layer: MapViewLayer, attachedViewStream: Observable<Optional<LayerViewContract>> ) : Presenter<LayerViewContract>(attachedViewStream) {

    lateinit var isSelectedStream : Observable<Boolean>

    override fun onCreate(subscriptions: CompositeDisposable) {
        super.onCreate(subscriptions)

        isSelectedStream = attachedViewStream
            .subscribeOnUiThread()
            .observeOnLogicThread()
            .switchMap { (view) ->
                view?.isSelectedStream?.subscribeOnUiThread() ?: Observable.never<Boolean>()
            }
            .observeOnLogicThread()
            .distinctUntilChanged()
            .share()
    }

    override fun onViewAttached(view: LayerViewContract, viewSubscriptions: CompositeDisposable) {
        super.onViewAttached(view, viewSubscriptions)

        val title = when(layer) {
            is MapViewLayer.Tile.Wms  -> layer.layer.name
            is MapViewLayer.Tile.Wmts -> layer.layer.name
            is MapViewLayer.Feature   -> layer.featureType.name
            else                      -> null
        } ?: ""

        doOnMainThread {
            view.title.accept(title)
            view.info.accept("Hi")
        }

        isSelectedStream
            .subscribeOnLogicThread()
            .observeOnUiThread()
            .distinctUntilChanged()
            .subscribe(view.isSelectedConsumer)
            .addTo(viewSubscriptions)
    }
}