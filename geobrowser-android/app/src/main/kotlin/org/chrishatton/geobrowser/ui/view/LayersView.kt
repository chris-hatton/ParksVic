package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewChildAttachEvent
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewChildDetachEvent
import com.jakewharton.rxbinding2.support.v7.widget.childAttachStateChangeEvents
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*
import kotlinx.android.synthetic.main.map_layer_feature.view.*
import opengis.process.AndroidOpenGisClient
import opengis.process.ServerListLoader
import opengis.process.load
import org.chrishatton.crosswind.ui.view.PresentedActivity
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.ui.contract.LayersViewContract
import org.chrishatton.geobrowser.ui.presenter.LayerPresenter
import org.chrishatton.geobrowser.ui.presenter.LayersPresenter

class LayersView : PresentedActivity<LayersViewContract,LayersPresenter>(), LayersViewContract {

    override var layers: Consumer<Iterable<LayerPresenter>> = Consumer { layers ->

    }

    override fun createPresenter(view: LayersViewContract): LayersPresenter {

        val servers = ServerListLoader.load( context = this, resource = R.raw.server_list )

        return LayersPresenter(
            clientProvider = ::AndroidOpenGisClient,
            serversStream  = Observable.just(servers)
        ).apply {
            this.view = view
        }
    }

    sealed class Exception : kotlin.Exception() {
        object UnsupportedType : Exception()
        object UnexpectedViewType : Exception()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            val sydney = LatLng(-34.0, 151.0)
            map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        layer_list.layoutManager = LinearLayoutManager(this).apply {
                orientation = LinearLayoutManager.VERTICAL
            }


        //layer_list.adapter = RxRecyclerAdapter<LayerViewHolder<MapViewLayer>,MapViewLayer>()

        layer_list.childAttachStateChangeEvents()
            .observeOn(Schedulers.computation())
            .scan(mutableSetOf<View>()) { views,event ->
                views.apply {
                    when(event) {
                        is RecyclerViewChildAttachEvent -> ::add
                        is RecyclerViewChildDetachEvent -> ::remove
                        else -> throw kotlin.Exception()
                    }(event.view())
                }
            }
            .map { views ->
                views.map { view ->
                    view.checkedFeatureTextView
                        .clicks()
                        .map { view }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //drawer_layout.closeDrawer(GravityCompat.START)


}
