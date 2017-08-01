package org.chrishatton.parksvic.view

import com.github.thomasnield.rxkotlinfx.toObservable
import com.lynden.gmapsfx.ClusteredGoogleMapView
import com.lynden.gmapsfx.javascript.`object`.ClusteredGoogleMap
import com.lynden.gmapsfx.javascript.`object`.Marker
import com.lynden.gmapsfx.javascript.`object`.MarkerOptions
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import javafx.scene.layout.BorderPane
import org.chrishatton.geojson.BoundingBox
import org.chrishatton.parksvic.data.model.Site
import org.chrishatton.parksvic.data.model.latLong
import org.chrishatton.parksvic.geojson.toBoundingBoxes
import org.chrishatton.parksvic.ui.contract.SitesViewContract
import tornadofx.*
import org.chrishatton.crosswind.util.log
import com.lynden.gmapsfx.javascript.`object`.MapTypeIdEnum
import com.lynden.gmapsfx.javascript.`object`.LatLong
import kotlin.reflect.jvm.internal.impl.load.kotlin.TypeSignatureMappingKt.mapType
import com.lynden.gmapsfx.javascript.`object`.MapOptions
import org.chrishatton.crosswind.ui.presenter.Presenter


class SitesView : PresentedView<SitesViewContract>("Parks in Victoria"), SitesViewContract {

    override val root: BorderPane by fxml()

    val clusteredMapView : ClusteredGoogleMapView by fxid()

    lateinit var map : ClusteredGoogleMap

    init {
        clusteredMapView.addMapInializedListener {
            log("Map Initialized")

            //Set the initial properties of the map.
            val mapOptions = MapOptions()

            mapOptions.center(LatLong(47.6097, -122.3331))
                    .mapType(MapTypeIdEnum.ROADMAP)
                    .overviewMapControl(false)
                    .panControl(false)
                    .rotateControl(false)
                    .scaleControl(false)
                    .streetViewControl(false)
                    .zoomControl(false)
                    .zoom(12.0)

            map = clusteredMapView.createMap(mapOptions)

            presenter.onMapini
        }

        clusteredMapView.addMapReadyListener {
            log("Map Ready")
        }
    }

    override val viewportBoundingBoxes: Observable<Array<BoundingBox>> by lazy {
        map.boundsProperty().toObservable().map { latLngBounds ->
            latLngBounds.toBoundingBoxes()
        }
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