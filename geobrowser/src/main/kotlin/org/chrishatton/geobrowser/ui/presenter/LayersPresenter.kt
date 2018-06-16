package org.chrishatton.geobrowser.ui.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import opengis.model.app.MapViewLayer
import opengis.model.app.OpenGisHttpServer
import opengis.process.OpenGisRequestProcessor
import opengis.rx.process.getMapViewLayers
import org.chrishatton.crosswind.rx.observeOnUiThread
import org.chrishatton.crosswind.rx.subscribeOnLogicThread
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.geobrowser.ui.contract.LayersViewContract

/**
 * Created by Chris on 19/01/2018.
 */
class LayersPresenter(
    val clientProvider : (OpenGisHttpServer)->OpenGisRequestProcessor,
    val serversStream : Observable<Iterable<OpenGisHttpServer>>
) : Presenter<LayersViewContract>() {

    private lateinit var mapViewLayersStream : Observable<Iterable<MapViewLayer>>

    override fun onCreate(subscriptions: CompositeDisposable) {
        super.onCreate(subscriptions)

        serversStream
            .flatMap { servers ->
                val layersStreams = servers.map { server ->
                    server.getMapViewLayers(clientProvider)
                }
                Observable.merge(layersStreams)
                        .scan(emptySet<MapViewLayer>()) { a,b -> a + b }
                        .map { it as Iterable<MapViewLayer> }
            }
            .replay(1)
            .apply { connect().addTo(subscriptions) }
    }

    override fun onViewAttached(view: LayersViewContract, viewSubscriptions: CompositeDisposable) {
        super.onViewAttached(view, viewSubscriptions)

        mapViewLayersStream
            .subscribeOnLogicThread()
            .map { mapViewLayers ->
                mapViewLayers.map { mapViewLayer -> LayerPresenter(mapViewLayer) }
            }
            .observeOnUiThread()
            .subscribe(view.layers)
            .addTo(viewSubscriptions)
    }
}