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
    var outcomes : MutableSet<Outcome<Set<MapViewLayer>>> = mutableSetOf()

    data class MapViewLayerProvision<Capabilities:Any>(val service: OpenGisService<Capabilities>, val extractMapLayers: (Capabilities)->Set<MapViewLayer>) {
        @Suppress("UNCHECKED_CAST")
        fun extractMapLayers(capabilities: Any) : Set<MapViewLayer> = extractMapLayers(capabilities as Capabilities)

        fun call(callback: Callback<Set<MapViewLayer>> ) {
            val request = service.createGetCapabilitiesRequest()
            processor.execute(request,service.capabilitiesClass) { outcome:Outcome<*> ->
                val mapViewLayerOutcome = when(outcome) {
                    is Outcome.Success<*> -> Outcome.Success(outcome.result?.let { extractMapLayers(it) } ?: emptySet() )
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

    mapViewLayerProvisions.forEach { provision ->
        provision.call { mapLayerOutcome ->
            outcomes.add(mapLayerOutcome)
            val isComplete = (outcomes.size==mapViewLayerProvisions.size)
            if(isComplete) {
                val successOutcomes = outcomes.filterIsInstance<Outcome.Success<Set<MapViewLayer>>>()
                val errorOutcomes   = outcomes.filterIsInstance<Outcome.Error<Set<MapViewLayer>>>()

                infix fun Outcome.Success<Set<MapViewLayer>>.add(other: Outcome.Success<Set<MapViewLayer>>) : Outcome.Success<Set<MapViewLayer>> {
                    return Outcome.Success( result = this.result + other.result )
                }

                val finalOutcome = errorOutcomes.firstOrNull() ?: successOutcomes.reduce(operation = Outcome.Success<Set<MapViewLayer>>::add)

                callback(finalOutcome)
            }
        }
    }
}