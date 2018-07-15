package org.chrishatton.geobrowser.ui.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import opengis.model.app.OpenGisHttpServer
import opengis.process.OpenGisRequestProcessor
import opengis.rx.process.getMapViewLayers
import org.chrishatton.crosswind.Crosswind
import org.chrishatton.crosswind.rx.observeOnLogicThread
import org.chrishatton.crosswind.rx.subscribeOnLogicThread
import org.chrishatton.crosswind.rx.subscribeOnNetworkThread
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.geobrowser.ui.contract.BrowserViewContract

class BrowserPresenter(
        var layerListPresenter : LayerListPresenter,
        var mapPresenter       : MapPresenter,
        val clientProvider     : (OpenGisHttpServer)-> OpenGisRequestProcessor,
        val view               : BrowserViewContract
    ) : Presenter<BrowserViewContract>(view) {

    private val serversStream = Observable.just(view.serverList)

    override fun onCreate(subscriptions: CompositeDisposable) {
        super.onCreate(subscriptions)

        serversStream
            .subscribeOnLogicThread()
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

//        attachedViewStream.subscribe { view ->
//            view.value?.let {
//                view.value?.serverList
//                //view.
//            }
//        }

        layerListPresenter.selectedLayers
            .subscribeOnLogicThread()
            .observeOnLogicThread()
            .subscribe(mapPresenter.mapLayersConsumer)
            .addTo(subscriptions)
    }
}
