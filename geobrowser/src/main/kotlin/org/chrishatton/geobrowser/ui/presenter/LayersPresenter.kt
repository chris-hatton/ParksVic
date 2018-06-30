package org.chrishatton.geobrowser.ui.presenter

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import opengis.model.app.MapViewLayer
import opengis.model.app.OpenGisHttpServer
import opengis.process.OpenGisRequestProcessor
import opengis.rx.process.getMapViewLayers
import org.chrishatton.crosswind.Crosswind
import org.chrishatton.crosswind.rx.observeOnUiThread
import org.chrishatton.crosswind.rx.subscribeOnLogicThread
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.Nullable
import org.chrishatton.geobrowser.ui.contract.LayerViewContract
import org.chrishatton.geobrowser.ui.contract.LayersViewContract

/**
 * Created by Chris on 19/01/2018.
 */
class LayersPresenter(
    val view : LayersViewContract,
    val clientProvider : (OpenGisHttpServer)->OpenGisRequestProcessor,
    val serversStream : Observable<Iterable<OpenGisHttpServer>>
) : Presenter<LayersViewContract>( view ) {

    private val layerPresenterCache : Cache<MapViewLayer, LayerPresenter> = CacheBuilder
            .newBuilder()
            .weakValues()
            .removalListener<MapViewLayer, LayerPresenter> { notification ->
                val presenter = notification.value
                presenter.destroy()
            }
            .initialCapacity(32)
            .build()

    override fun onViewAttached(view: LayersViewContract, viewSubscriptions: CompositeDisposable) {
        super.onViewAttached(view, viewSubscriptions)

        val loadedMapViewLayers : Observable<Iterable<MapViewLayer>> =
            serversStream.flatMap { servers ->
                val layersStreams = servers.map { server ->
                    server.getMapViewLayers(clientProvider)
                        .doOnError { e -> Crosswind.environment.logger(e.localizedMessage) }
                        .onErrorResumeNext( Observable.never() )
                }
                return@flatMap Observable.merge(layersStreams)
            }

        loadedMapViewLayers
            .subscribeOnLogicThread()
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
            //.map { it as Iterable<LayerPresenter> }
            .doOnNext { Crosswind.environment.logger("There are ${it.count()} layers.") }
            .observeOnUiThread()
            .subscribe(view.layerPresentersConsumer)
            .addTo(viewSubscriptions)
    }
}