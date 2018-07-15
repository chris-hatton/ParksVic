package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_layer_list.*
import opengis.model.app.MapViewLayer
import opengis.process.AndroidOpenGisClient
import org.chrishatton.crosswind.ui.view.PresentedFragment
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.rx.RxRecyclerAdapter
import org.chrishatton.geobrowser.ui.contract.LayerListViewContract
import org.chrishatton.geobrowser.ui.contract.LayerViewContract
import org.chrishatton.geobrowser.ui.presenter.LayerListPresenter
import org.chrishatton.geobrowser.ui.presenter.LayerPresenter

class LayerListView : PresentedFragment<LayerListViewContract,LayerListPresenter>(), LayerListViewContract {

    private val layerPresentersSubject = PublishSubject.create<Iterable<LayerPresenter>>()
    override var layerPresentersConsumer: Consumer<Iterable<LayerPresenter>> = Consumer { layers ->
        layerPresentersSubject.onNext(layers)
    }

    private val mapViewLayersStream : Observable<Iterable<MapViewLayer>> = layerPresentersSubject.map { layerPresenters ->
        layerPresenters.map { layerPresenter -> layerPresenter.layer }
    }

    private val layerListAdapter = RxRecyclerAdapter<LayerView<MapViewLayer>,MapViewLayer>(
            itemsStream = mapViewLayersStream,
            createViewHolder = { viewGroup,type ->
                when(type) {
                    0 -> LayerView.Tile   (this@LayerListView.activity.layoutInflater,viewGroup)
                    1 -> LayerView.Feature(this@LayerListView.activity.layoutInflater,viewGroup)
                    else -> throw Exception()
                }
            }
    )

    override var layerViewBindingsStream: Observable<Map<MapViewLayer, LayerViewContract>> =
            layerListAdapter.layerViewBindingStream
                    //.doOnNext { Crosswind.environment.logger("$it") }
                    .scan(emptyMap<MapViewLayer, LayerViewContract>()) {  map,layerToView -> mapOf(layerToView) + map }
                    .share()

    override fun createPresenter(): LayerListPresenter = LayerListPresenter(
            view                = this,
            mapViewLayersStream = mapViewLayersStream
        )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_layer_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layer_list.apply {
            layoutManager = LinearLayoutManager(this@LayerListView.activity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            adapter = layerListAdapter
        }
    }
}
