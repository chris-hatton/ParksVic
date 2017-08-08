package org.chrishatton.parksvic.ui.view

import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
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
import org.chrishatton.parksvic.rx.panelState
import org.chrishatton.parksvic.ui.contract.DetailLevel
import org.chrishatton.parksvic.ui.contract.SitesViewContract
import org.chrishatton.parksvic.ui.presenter.SitesPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.Cluster
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.chrishatton.parksvic.rx.slidePanelOverlapHeight
import org.chrishatton.parksvic.ui.contract.ZoomLevel
import com.google.maps.android.clustering.ClusterManager
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import org.chrishatton.crosswind.rx.doOnMainThread
import org.chrishatton.crosswind.util.log
import org.chrishatton.parksvic.geojson.toBoundingBoxes
import org.chrishatton.parksvic.model.SiteItem


class SitesActivity : PresentedActivity<SitesViewContract, SitesPresenter>(), SitesViewContract {

    override fun createPresenter( view: SitesViewContract ): SitesPresenter = SitesPresenter().apply { this.view = view  }

    private val subscriptions = CompositeDisposable()

    private lateinit var clusterManager : ClusterManager<SiteItem>
    private lateinit var map : GoogleMap

    init {
        environment = androidEnvironment
        assertMainThread()
        RxJavaPlugins.setErrorHandler { e ->
            log(e.stackTrace.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.activity_parks)

        super.onCreate(savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment    = this.fragmentManager.findFragmentById(R.id.map_fragment   ) as MapFragment
        val detailFragment = this.fragmentManager.findFragmentById(R.id.detail_fragment) as SiteDetailFragment

        presenter.detailPresenter = detailFragment.presenter

        mapFragment.getMapAsync { map -> onMapInitialised(map) }

        sliding_layout.setDragView      ( detailFragment.view.details_header)
        sliding_layout.setScrollableView( detailFragment.view.details_table_scroll_view)
    }

    private fun onMapInitialised( map: GoogleMap ) {
        assertMainThread()

        this.map = map

        clusterManager = ClusterManager<SiteItem>(this, map)

        map.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
            val bounds : LatLngBounds = map.projection.visibleRegion.latLngBounds
            val boundingBoxes : Array<BoundingBox> = bounds.toBoundingBoxes()
            boundingBoxSubject.onNext( boundingBoxes )
        }

        map.setOnMarkerClickListener(clusterManager)

        clusterManager.setOnClusterClickListener { cluster: Cluster<SiteItem>? ->
            true
        }

        clusterManager.setOnClusterItemClickListener { siteItem: SiteItem? ->
            doOnLogicThread { presenter.onSelectSite(siteItem?.site) }
            true
        }

        sliding_layout.slidePanelOverlapHeight().subscribe { height ->
            assertMainThread()
            map.setPadding(0,0,0,height)
        }.addTo(subscriptions)

        doOnLogicThread {
            presenter.onMapInitialized()
        }
    }

    override fun onDestroy() {
        subscriptions.dispose()
        super.onDestroy()
    }

    private val boundingBoxSubject = BehaviorSubject.create<Array<BoundingBox>>()
    override val viewportBoundingBoxes: Observable<Array<BoundingBox>> = boundingBoxSubject

    override fun setDisplayedSites(sites: List<Site> ) {
        assertMainThread()

        clusterManager.clearItems()

        doOnLogicThread {
            val siteItems : List<SiteItem> = sites.map(::SiteItem)
            doOnMainThread {
                clusterManager.addItems(siteItems)
            }
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

        val location : CameraUpdate = CameraUpdateFactory.newLatLngZoom( coordinate, googleMapZoomLevel )

        map.animateCamera(location)

        if( map.mapType != mapType ) {
            map.mapType = mapType
        }
    }

    override var isMapInteractionEnabled: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}
