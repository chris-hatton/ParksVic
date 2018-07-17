package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import geojson.Feature
import kotlinx.android.synthetic.main.fragment_map.*
import opengis.process.TileProvider
import org.chrishatton.crosswind.ui.view.PresentedFragment
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.ui.contract.MapViewContract
import org.chrishatton.geobrowser.ui.presenter.MapPresenter

class MapView : PresentedFragment<MapViewContract, MapPresenter>(), MapViewContract {

    override fun createPresenter(): MapPresenter = MapPresenter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            val sydney = LatLng(-34.0, 151.0)
            map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun addTileLayer(provider: TileProvider<*>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeTileLayer(provider: TileProvider<*>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addFeature(feature: Feature) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeFeature(feature: Feature): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}