package org.chrishatton.parksvic.ui.view

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_parks.*
import kotlinx.android.synthetic.main.fragment_site_detail.view.*
import org.chrishatton.crosswind.androidEnvironment
import org.chrishatton.crosswind.environment
import org.chrishatton.crosswind.rx.assertMainThread
import org.chrishatton.crosswind.rx.doOnLogicThread
import org.chrishatton.crosswind.ui.view.PresentedActivity
import org.chrishatton.crosswind.util.Nullable
import org.chrishatton.geojson.BoundingBox
import org.chrishatton.parksvic.R
import org.chrishatton.parksvic.data.model.Site
import org.chrishatton.parksvic.geojson.toBoundingBoxes
import org.chrishatton.parksvic.panelState
import org.chrishatton.parksvic.ui.contract.DetailLevel
import org.chrishatton.parksvic.ui.contract.SitesViewContract
import org.chrishatton.parksvic.ui.presenter.SitesPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import org.chrishatton.parksvic.ui.contract.ZoomLevel


class SitesActivity : PresentedActivity<SitesViewContract, SitesPresenter>(), SitesViewContract {

    override fun createPresenter( view: SitesViewContract ): SitesPresenter = SitesPresenter().apply { this.view = view  }

    private var mapSubject: BehaviorSubject<GoogleMap> = BehaviorSubject.create<GoogleMap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_parks)
        super.onCreate(savedInstanceState)

        environment = androidEnvironment
        assertMainThread()



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment    = this.fragmentManager.findFragmentById(R.id.map_fragment) as MapFragment
        val detailFragment = this.fragmentManager.findFragmentById(R.id.detail_fragment) as SiteDetailFragment

        presenter.detailPresenter = detailFragment.presenter

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

        sliding_layout.setDragView      ( detailFragment.view.details_header)
        sliding_layout.setScrollableView( detailFragment.view.details_table_scroll_view)
    }

    private val boundingBoxSubject = BehaviorSubject.create<Array<BoundingBox>>()

    override val viewportBoundingBoxes: Observable<Array<BoundingBox>> = boundingBoxSubject

    private var markers : List<Marker> = emptyList()

    override fun setDisplayedSites(sites: List<Site> ) {

        if(!mapSubject.hasValue()) { return }

        val map : GoogleMap = mapSubject.value

        markers.forEach { marker -> marker.remove() }

        val markerMap : Map<Marker,Site> = sites.associateBy { site ->
            val markerOptions = MarkerOptions()
                .position( LatLng( site.latitude!!, site.longitude!! ) )
                .title( site.name )

            map.addMarker(markerOptions)
        }

        map.setOnMarkerClickListener { marker ->
            val site : Site = markerMap[marker] ?: return@setOnMarkerClickListener false
            presenter.onSelectSite( site )
            true
        }
    }

    override val userSelectedDetailLevel: Observable<DetailLevel> get() {
        return sliding_layout.panelState().map { panelState ->
            val detailLevel : DetailLevel? = when( panelState ) {
                SlidingUpPanelLayout.PanelState.EXPANDED    -> DetailLevel.FULL
                SlidingUpPanelLayout.PanelState.COLLAPSED   -> DetailLevel.MINIMAL
                SlidingUpPanelLayout.PanelState.ANCHORED    -> DetailLevel.MEDIUM
                SlidingUpPanelLayout.PanelState.HIDDEN      -> DetailLevel.NONE
                SlidingUpPanelLayout.PanelState.DRAGGING    -> null
            }
            Nullable(detailLevel)
        }.filter { (detailLevel) ->
            detailLevel != null
        }.map { (detailLevel) -> detailLevel!! }
    }

    override fun focusSite( site: Site, zoomLevel: ZoomLevel ) {
        assertMainThread()

        val map : GoogleMap = mapSubject.value
        val coordinate = LatLng( site.latitude!!, site.longitude!! )

        val googleMapZoomLevel : Float = when( zoomLevel ) {
            ZoomLevel.NEAR   -> 18.0f
            ZoomLevel.MEDIUM -> 14.0f
            ZoomLevel.FAR    -> 10.0f
        }

        val mapType : Int= when( zoomLevel ) {
            ZoomLevel.FAR,
            ZoomLevel.MEDIUM -> GoogleMap.MAP_TYPE_TERRAIN
            ZoomLevel.NEAR   -> GoogleMap.MAP_TYPE_SATELLITE

        }

        val location = CameraUpdateFactory.newLatLngZoom( coordinate, googleMapZoomLevel )

        map.animateCamera(location)
        map.mapType = mapType
    }

    override var isMapInteractionEnabled: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

}
