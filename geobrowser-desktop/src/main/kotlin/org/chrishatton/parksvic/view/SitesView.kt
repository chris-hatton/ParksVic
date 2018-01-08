package org.chrishatton.geobrowser.view

import com.github.thomasnield.rxkotlinfx.toObservable
import com.lynden.gmapsfx.ClusteredGoogleMapView
import com.lynden.gmapsfx.javascript.`object`.*
import com.lynden.gmapsfx.javascript.event.MapStateEventType
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import javafx.scene.layout.BorderPane
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.ui.view.PresentedView
import org.chrishatton.crosswind.util.log
import org.chrishatton.geojson.BoundingBox
import org.chrishatton.geobrowser.data.model.Site
import org.chrishatton.geobrowser.data.model.latLong
import org.chrishatton.geobrowser.geojson.toBoundingBoxes
import org.chrishatton.geobrowser.ui.contract.SitesViewContract
import org.chrishatton.geobrowser.ui.presenter.SitesPresenter
import java.util.concurrent.TimeUnit


class SitesView : PresentedView<SitesViewContract,SitesPresenter>("Parks in Victoria"), SitesViewContract {

    override fun createPresenter(): SitesPresenter = SitesPresenter()

    override val root: BorderPane by fxml()

    val clusteredMapView : ClusteredGoogleMapView by fxid()

    lateinit var map : ClusteredGoogleMap

    init {
        clusteredMapView.addMapInializedListener {
            log("Map Initialized")

            //Set the initial properties of the map.
            val mapOptions = MapOptions()
                    .mapType(MapTypeIdEnum.TERRAIN)
                    .overviewMapControl(false)
                    .panControl(false)
                    .rotateControl(false)
                    .scaleControl(false)
                    .streetViewControl(false)
                    .zoomControl(true)
                    .center( LatLong(0.0,0.0) )
                    .zoom(4.0)

            map = clusteredMapView.createMap(mapOptions)

        }

        clusteredMapView.addMapReadyListener {
            log("Map Ready")

            registerEventHandlers()

            presenter.onMapInitialized()

            map.fitBounds(LatLongBounds(LatLong(0.0,0.0),LatLong(30.0,30.0)))

        }
    }

    private fun registerEventHandlers() {
        MapStateEventType.values().forEach {
            map.addStateEventHandler( it ) { println("Event: ${it.name}") }
        }

        map.centerProperty().addListener { c -> println("Center: $c") }
        map.zoomProperty()  .addListener { z -> println("Zoom: $z"  ) }
        map.boundsProperty().addListener { b -> println("Bounds: $b") }

    }

    override val viewportBoundingBoxes: Observable<Array<BoundingBox>> by lazy {
        map.centerProperty().toObservable()
                .debounce(1,TimeUnit.SECONDS)
                .map { map.bounds.toBoundingBoxes() }
    }

    override val viewportSitesConsumer: Consumer<Array<Site>> = Consumer { sites ->

        val siteMarkers = sites.map { site ->
            val options = MarkerOptions()
                    .title(site.name ?: "Unnamed site")
                    .position(site.latLong)
                    .label( site.label ?: "Unknown label")

            Marker(options)
        }

        map.clearMarkers()

        siteMarkers.forEach( map::addClusterableMarker )
    }
}