package opengis.ui.presenter

import geojson.Feature
import opengis.model.app.request.wms.GetMap
import opengis.process.OpenGisRequestProcessor
import opengis.ui.WmsTileProvider
import opengis.ui.WmtsTileProvider
import opengis.model.app.MapViewLayer
import opengis.ui.view.MapViewContract
import org.chrishatton.crosswind.ui.presenter.Presenter
import kotlin.properties.Delegates

/**
 * Created by Chris on 19/01/2018.
 */
class MapPresenter(override val view: MapViewContract) : Presenter<MapViewContract>() {

    private val wmtsLayerTileProviders : MutableMap<MapViewLayer.Tile.Wmts, WmtsTileProvider> = mutableMapOf()
    private val wmsLayerTileProviders  : MutableMap<OpenGisRequestProcessor,WmsTileProvider> = mutableMapOf()
    private val featuresByType         : MutableMap<MapViewLayer.Feature,   Set<Feature>    > = mutableMapOf()

    var mapLayers : List<MapViewLayer> by Delegates.observable(emptyList()) { _, oldLayers, newLayers ->

        val oldWmtsLayers = oldLayers.filterIsInstance<MapViewLayer.Tile.Wmts>()
        val newWmtsLayers = newLayers.filterIsInstance<MapViewLayer.Tile.Wmts>()

        (oldWmtsLayers - newWmtsLayers) // Layers to Remove
            .mapNotNull(wmtsLayerTileProviders::remove)
            .forEach { tileProvider -> view.removeTileLayer(tileProvider) }

        (newWmtsLayers - oldWmtsLayers) // Layers to Add
            .associateTo(wmtsLayerTileProviders) { layer ->
                val tileProvider = WmtsTileProvider(layer.requestProcessor,layer.layer.name)
                view.addTileLayer(tileProvider)
                layer to tileProvider
            }

        val oldWmsLayersByProcessor = oldLayers
                .filterIsInstance<MapViewLayer.Tile.Wms>()
                .groupBy { it.requestProcessor }

        val newWmsLayersByProcessor = newLayers
                .filterIsInstance<MapViewLayer.Tile.Wms>()
                .groupBy { it.requestProcessor }

        (oldWmsLayersByProcessor.keys + newWmsLayersByProcessor.keys)  // All processors to consider
                .forEach { processor ->
                    val oldProcessorLayers = oldWmsLayersByProcessor[processor]
                    val newProcessorLayers = newWmsLayersByProcessor[processor]
                    if(oldProcessorLayers != newProcessorLayers) {
                        wmsLayerTileProviders.remove(processor)?.let(view::removeTileLayer)

                        val newProcessorTileProvider = newProcessorLayers?.let { layers ->
                            val styledLayers = layers.map { GetMap.StyledLayer(it.layer) }
                            val tileProvider = WmsTileProvider(processor,styledLayers)
                            wmsLayerTileProviders[processor] = tileProvider
                            tileProvider
                        }

                        newProcessorTileProvider?.let(view::addTileLayer)
                    }
                }

        /*
        (oldWmsLayersByProcessor.keys - newWmsLayersByProcessor.keys) // Processors to remove
                .map(wmsLayerTileProviders::remove)
                .filterNotNull()
                .forEach { (_,tileProvider) -> view.removeTileLayer(tileProvider) }

        (newWmsLayersByProcessor.keys - oldWmsLayersByProcessor.keys) // Processors to add
                .associateTo(wmsLayerTileProviders) { processor ->
                    val layers = newWmsLayersByProcessor[processor]!!
                    val styledLayers : List<GetMap.StyledLayer> = layers.map { GetMap.StyledLayer(it.layer) }
                    val tileProvider = WmsTileProvider(processor,styledLayers)
                    processor to (layers to tileProvider)
                }
                */

        val oldFeatureTypes = oldLayers.filterIsInstance<MapViewLayer.Feature>()
        val newFeatureTypes = newLayers.filterIsInstance<MapViewLayer.Feature>()


    }

    override fun onCreate() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun onDestroy() {}
}