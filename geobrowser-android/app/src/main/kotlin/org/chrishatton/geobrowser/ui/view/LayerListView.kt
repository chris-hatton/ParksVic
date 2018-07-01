package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*
import opengis.model.app.MapViewLayer
import opengis.process.AndroidOpenGisClient
import opengis.process.ServerListLoader
import opengis.process.load
import org.chrishatton.crosswind.ui.view.PresentedActivity
import org.chrishatton.crosswind.ui.view.PresentedFragment
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.rx.RxRecyclerAdapter
import org.chrishatton.geobrowser.ui.contract.LayerViewContract
import org.chrishatton.geobrowser.ui.contract.LayerListViewContract
import org.chrishatton.geobrowser.ui.presenter.LayerPresenter
import org.chrishatton.geobrowser.ui.presenter.LayerListPresenter

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

    override fun createPresenter(): LayerListPresenter {

        val servers = ServerListLoader.load( context = this.activity, resource = R.raw.server_list )

        return LayerListPresenter(
            view           = this,
            clientProvider = ::AndroidOpenGisClient,
            serversStream  = Observable.just(servers)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layer_list.layoutManager = LinearLayoutManager(this.activity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }

        layer_list.adapter = layerListAdapter
    }
}
