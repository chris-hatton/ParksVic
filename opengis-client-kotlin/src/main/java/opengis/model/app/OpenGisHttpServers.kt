package opengis.model.app

import opengis.process.Callback
import opengis.process.OpenGisRequestProcessor
import opengis.process.Outcome

import opengis.model.app.request.wms.Layer as WmsLayer
import opengis.model.app.request.wmts.Layer as WmtsLayer

/**
 * Retrieve a list of all MapViewLayers from this OpenGIS HTTP server.
 */
fun OpenGisHttpServer.getMapViewLayers(
        clientProvider : (OpenGisHttpServer)-> OpenGisRequestProcessor,
        callback       : Callback<Set<MapViewLayer>>
    ) {

    val processor = clientProvider(this)

    /**
     *  */
    data class MapViewLayerProvision<Capabilities:Any>(val service: OpenGisService<Capabilities>, val extractMapLayers: (Capabilities)->Set<MapViewLayer>) {
        @Suppress("UNCHECKED_CAST")
        //fun extractMapLayers(capabilities: Any) : Set<MapViewLayer> = this.extractMapLayers(capabilities as Capabilities)

        fun call(callback: Callback<Set<MapViewLayer>> ) {
            val request = service.createGetCapabilitiesRequest()
            processor.execute(request,service.capabilitiesClass) { outcome:Outcome<*> ->
                val mapViewLayerOutcome = when(outcome) {
                    is Outcome.Success<*> -> Outcome.Success(outcome.result?.let { extractMapLayers(it as Capabilities) } ?: emptySet() )
                    is Outcome.Error      -> Outcome.Error<Set<MapViewLayer>>(outcome.error)
                }
                callback(mapViewLayerOutcome)
            }
        }
    }

    val wmsMapViewLayerProvision = MapViewLayerProvision(
            service = OpenGisService.WebMapService,
            extractMapLayers = { capabilities ->
        capabilities.capability?.layer?.layers?.let { layers ->
            layers.mapNotNull { layer -> layer.name }
                .map(::WmsLayer)
                .map { MapViewLayer.Tile.Wms(processor,it) }
                .toSet()
        } ?: emptySet()
    })

    val mapViewLayerProvisions : Set<MapViewLayerProvision<*>> = setOf(
            wmsMapViewLayerProvision
    )

    Outcome.fold(
        inputs   = mapViewLayerProvisions,
        caller   = MapViewLayerProvision<*>::call,
        initial  = emptySet(),
        reduce   = { a,b -> a + b },
        callback = callback
    )
}