package org.chrishatton.geobrowser.ui.presenter

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.rxkotlin.addTo
import opengis.model.app.MapViewLayer
import opengis.model.app.OpenGisHttpServer
import opengis.process.OpenGisRequestProcessor
import opengis.rx.process.getMapViewLayers
import org.chrishatton.crosswind.Crosswind
import org.chrishatton.crosswind.rx.*
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.Nullable
import org.chrishatton.geobrowser.ui.contract.LayerListViewContract

/**
 * Created by Chris on 19/01/2018.
 */
class LayerListPresenter(
        val view : LayerListViewContract,
        val clientProvider : (OpenGisHttpServer)->OpenGisRequestProcessor,
        val serversStream : Observable<Iterable<OpenGisHttpServer>>
) : Presenter<LayerListViewContract>( view ) {

    private val layerPresenterCache : Cache<MapViewLayer, LayerPresenter> = CacheBuilder
            .newBuilder()
            .weakValues()
            .removalListener<MapViewLayer, LayerPresenter> { notification ->
                val presenter = notification.value
                doOnLogicThread {
                    presenter.destroy()
                }
            }
            .initialCapacity(32)
            .build()

    private val layerPresentersStream : ConnectableObservable<Iterable<LayerPresenter>> = serversStream
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
        .observeOnLogicThread()
        .map { mapViewLayers ->
            mapViewLayers.map { mapViewLayer ->
                layerPresenterCache.get(mapViewLayer) {
                    val viewStream = view.layerViewBindingsStream.map { layersToViews ->
                        val layerView = layersToViews[mapViewLayer]
                        Nullable(layerView)
                    }
                    LayerPresenter(mapViewLayer,viewStream).also { it.create() }
                }
            }
        }
        .scan(emptySet<LayerPresenter>()) { a,b -> a + b }
        .map { it as Iterable<LayerPresenter> }
        .publish()

    //private val mapPresenter : MapPresenter = MapPresenter(view.map)

    override fun onViewAttached(view: LayerListViewContract, viewSubscriptions: CompositeDisposable) {
        super.onViewAttached(view, viewSubscriptions)

        layerPresentersStream
            .observeOnUiThread()
            .subscribe(view.layerPresentersConsumer)
            .addTo(viewSubscriptions)
    }
}