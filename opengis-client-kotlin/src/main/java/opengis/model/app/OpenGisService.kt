package opengis.model.app

import opengis.model.app.request.OpenGisRequest
import opengis.model.app.request.csw.CatalogueServiceForWebRequest
import opengis.model.app.request.wcs.WebCoverageServiceRequest
import opengis.model.app.request.wfs.WebFeatureServiceRequest
import opengis.model.app.request.wms.WebMapServiceRequest
import opengis.model.app.request.wmts.WebMapTileServiceRequest
import opengis.model.app.request.wms.GetCapabilities as GetWmsCapabilities
import opengis.model.app.request.wmts.GetCapabilities as GetWmtsCapabilities
import opengis.model.app.request.wfs.GetCapabilities as GetWfsCapabilities
import opengis.model.app.request.wcs.GetCapabilities as GetWcsCapabilities
import opengis.model.app.request.csw.GetCapabilities as GetCswCapabilities
import opengis.model.transport.xml.csw.CswCapabilities
import opengis.model.transport.xml.wcs.WcsCapabilities
import opengis.model.transport.xml.wfs.WfsCapabilities
import opengis.model.transport.xml.wms.WmsCapabilities
import opengis.model.transport.xml.wmts.WmtsCapabilities
import opengis.process.OpenGisRequestProcessor
import kotlin.reflect.KClass

/**
 * Created by Chris on 23/09/2017.
 */
sealed class OpenGisService<Capabilities:Any> {

    abstract fun createGetCapabilitiesRequest() : OpenGisRequest<Capabilities>
    abstract val capabilitiesClass : KClass<Capabilities>

    object WebFeatureService : OpenGisService<WfsCapabilities>() {
        override fun createGetCapabilitiesRequest() : OpenGisRequest<WfsCapabilities> = GetWfsCapabilities()
        override val capabilitiesClass: KClass<WfsCapabilities> = WfsCapabilities::class
    }

    object WebMapService : OpenGisService<WmsCapabilities>() {
        override fun createGetCapabilitiesRequest() = GetWmsCapabilities()
        override val capabilitiesClass: KClass<WmsCapabilities> = WmsCapabilities::class
    }

    object WebMapTileService : OpenGisService<WmtsCapabilities>() {
        override fun createGetCapabilitiesRequest() = GetWmtsCapabilities()
        override val capabilitiesClass: KClass<WmtsCapabilities> = WmtsCapabilities::class
    }

    object WebCoverageService : OpenGisService<WcsCapabilities>() {
        override fun createGetCapabilitiesRequest() = GetWcsCapabilities()
        override val capabilitiesClass: KClass<WcsCapabilities> = WcsCapabilities::class
    }

    object CatalogueServicesForWeb : OpenGisService<CswCapabilities>() {
        override fun createGetCapabilitiesRequest() = GetCswCapabilities()
        override val capabilitiesClass: KClass<CswCapabilities> = CswCapabilities::class
    }

//    fun getCapabilities( processor: OpenGisRequestProcessor, callback: Callback<Capabilities> ) = processor.execute(
//            request    = createGetCapabilitiesRequest(),
//            resultType = this.capabilitiesClass,
//            callback   = callback
//        )

    companion object {

        /**
         * Returns a set of all supported OpenGIS service types.
         */
        fun values() : Set<OpenGisService<*>> = setOf(
            WebFeatureService,
            WebMapService,
            WebMapTileService,
            WebCoverageService,
            CatalogueServicesForWeb
        )

        /**
         * Maps a given `OpenGisRequest` to the OpenGIS service by which it should be handled.
         */
        fun <Result> `for`( request: OpenGisRequest<Result>) : OpenGisService<*> = when(request){
            is WebMapServiceRequest<*>          -> OpenGisService.WebMapService
            is WebMapTileServiceRequest<*>      -> OpenGisService.WebMapTileService
            is WebFeatureServiceRequest<*>      -> OpenGisService.WebFeatureService
            is WebCoverageServiceRequest<*>     -> OpenGisService.WebCoverageService
            is CatalogueServiceForWebRequest<*> -> OpenGisService.WebMapService

            else -> throw OpenGisRequestProcessor.Exception.UnhandledRequestType
        }
    }
}