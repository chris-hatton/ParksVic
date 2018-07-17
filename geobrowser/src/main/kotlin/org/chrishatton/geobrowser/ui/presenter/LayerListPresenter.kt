package org.chrishatton.geobrowser.ui.presenter

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.observables.ConnectableObservable
import io.reactivex.rxkotlin.addTo
import opengis.model.app.MapViewLayer
import org.chrishatton.crosswind.rx.*
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.Nullable
import org.chrishatton.crosswind.util.log
import org.chrishatton.geobrowser.ui.contract.LayerListViewContract

/**
 * Created by Chris on 19/01/2018.
 */
class LayerListPresenter(
        val view : LayerListViewContract
) : Presenter<LayerListViewContract>( view ) {

    private val layerListRelay : Relay<Iterable<MapViewLayer>> = BehaviorRelay.createDefault(emptySet())
    val layerListConsumer : Consumer<Iterable<MapViewLayer>> = layerListRelay

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

    private val layerPresentersStream : ConnectableObservable<Iterable<LayerPresenter>> = layerListRelay
            .logOnNext { "HO!!!" }
            .observeOnLogicThread()
        .map { mapViewLayers:Iterable<MapViewLayer> ->
            mapViewLayers.map(this::getOrCreateLayerPresenter)
        }
        .scan(emptySet<LayerPresenter>()) { a,b -> a + b }
        .map { it as Iterable<LayerPresenter> }
        .publish()

    val selectedLayers : Observable<Iterable<MapViewLayer>> = layerPresentersStream.switchMap { layerPresenters ->

        val visibilityToLayersStream : Iterable<Observable<Pair<MapViewLayer,Boolean>>> =
            layerPresenters.map { layerPresenter ->
                layerPresenter.isVisibleStream
                    .map { layerPresenter.layer to it }
            }

        Observable.combineLatest(visibilityToLayersStream) { visibilityToLayers ->
            (visibilityToLayers as Array<Pair<MapViewLayer,Boolean>>)
                .filter { (_,isVisible) -> isVisible }
                .map    { (layer,_)     -> layer     }
        }
    }

    private fun getOrCreateLayerPresenter(mapViewLayer: MapViewLayer ) = layerPresenterCache.get(mapViewLayer) {
        val viewStream = view.layerViewBindingsStream.map { layersToViews ->
            val layerView = layersToViews[mapViewLayer]
            Nullable(layerView)
        }
        LayerPresenter(mapViewLayer,viewStream).also { it.create() }
    }

    override fun onViewAttached(view: LayerListViewContract, viewSubscriptions: CompositeDisposable) {
        super.onViewAttached(view, viewSubscriptions)

        layerPresentersStream
                .observeOnUiThread()
                .logOnNext { "${it.count()} layers" }
                .subscribe(view.layerPresentersConsumer)
                .addTo(viewSubscriptions)

        layerPresentersStream
                .connect()
                .addTo(viewSubscriptions)
    }
}