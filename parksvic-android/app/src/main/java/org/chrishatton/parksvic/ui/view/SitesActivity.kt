package org.chrishatton.parksvic.ui.view

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.chrishatton.crosswind.androidEnvironment
import org.chrishatton.crosswind.environment
import org.chrishatton.crosswind.rx.assertMainThread
import org.chrishatton.crosswind.rx.doOnLogicThread
import org.chrishatton.crosswind.ui.view.PresentedActivity
import org.chrishatton.geojson.BoundingBox
import org.chrishatton.parksvic.R
import org.chrishatton.parksvic.data.model.Site
import org.chrishatton.parksvic.geojson.toBoundingBoxes
import org.chrishatton.parksvic.ui.contract.SitesViewContract
import org.chrishatton.parksvic.ui.presenter.SitesPresenter


class SitesActivity : PresentedActivity<SitesViewContract, SitesPresenter>(), SitesViewContract {

    override fun createPresenter(): SitesPresenter = SitesPresenter()

    private var mapSubject: BehaviorSubject<GoogleMap> = BehaviorSubject.create<GoogleMap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        environment = androidEnvironment
        assertMainThread()

        setContentView(R.layout.activity_parks)

        presenter.view = this

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { map ->

            map.setOnCameraIdleListener {
                //doOnLogicThread {
                    val bounds : LatLngBounds = mapSubject.value.projection.visibleRegion.latLngBounds
                    val boundingBoxes : Array<BoundingBox> = bounds.toBoundingBoxes()
                    boundingBoxSubject.onNext( boundingBoxes )
                //}
            }

            doOnLogicThread {
                mapSubject.onNext(map)
                presenter.onMapInitialized()
            }
        }
    }

    private val boundingBoxSubject = BehaviorSubject.create<Array<BoundingBox>>()

    override val viewportBoundingBoxes: Observable<Array<BoundingBox>> = boundingBoxSubject

    private var markers : List<Marker> = emptyList()

    override fun onViewportSitesReceived( sites: List<Site> ) {

        if(!mapSubject.hasValue()) { return }

        val map : GoogleMap = mapSubject.value
        markers.forEach { marker -> marker.remove() }

        val markerOptions = sites.map { site ->
            MarkerOptions()
                    .position( LatLng( site.latitude!!, site.longitude!! ) )
                    .title( site.name )
                    .snippet( site.facilityType )
        }


        markers = markerOptions.map { markerOption -> map.addMarker(markerOption) }
    }
}
