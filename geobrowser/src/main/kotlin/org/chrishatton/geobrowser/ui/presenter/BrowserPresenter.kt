package org.chrishatton.geobrowser.ui.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import opengis.model.app.OpenGisHttpServer
import opengis.process.OpenGisRequestProcessor
import opengis.rx.process.getMapViewLayers
import org.chrishatton.crosswind.Crosswind
import org.chrishatton.crosswind.rx.*
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.log
import org.chrishatton.geobrowser.ui.contract.BrowserViewContract

class BrowserPresenter(
        var layerListPresenter : LayerListPresenter,
        var mapPresenter       : MapPresenter,
        val clientProvider     : (OpenGisHttpServer) -> OpenGisRequestProcessor,
        val view               : BrowserViewContract
    ) : Presenter<BrowserViewContract>(view) {

    private val serversStream = Observable.just(view.serverList)

    override fun onCreate(subscriptions: CompositeDisposable) {
        super.onCreate(subscriptions)

        serversStream
            .subscribeOnUiThread()
            .observeOnLogicThread()
            .flatMap { servers ->
                val layersStreams = servers.map { server ->
                    server.getMapViewLayers(clientProvider)
                        .subscribeOnNetworkThread()
                        .doOnError { e -> Crosswind.environment.logger(e.localizedMessage) }
                        .onErrorResumeNext( Observable.never() )
                }
                return@flatMap Observable.merge(layersStreams)
            }
            .logOnNext { "WUT ${it.size}" }
            .subscribe( layerListPresenter.layerListConsumer, Consumer { e -> log(e.toString()) } )
            .addTo(subscriptions)

        layerListPresenter.selectedLayers
            .subscribeOnLogicThread()
            .observeOnLogicThread()
            .logOnNext { "Selected layers: $it" }
            .subscribe(mapPresenter.mapLayersConsumer, Consumer { e -> log(e.toString()) } )
            .addTo(subscriptions)
    }
}
