package opengis.model.app

import opengis.model.transport.xml.wfs.FeatureType
import opengis.process.OpenGisRequestProcessor
import opengis.model.app.request.wms.Layer as WmsLayer
import opengis.model.app.request.wmts.Layer as WmtsLayer

/**
 * Created by Chris on 04/02/2018.
 */
sealed class MapViewLayer(val requestProcessor: OpenGisRequestProcessor) {

    abstract class Tile(requestProcessor: OpenGisRequestProcessor) : MapViewLayer(requestProcessor)  {
        class Wmts(requestProcessor: OpenGisRequestProcessor, val layer: WmtsLayer) : Tile(requestProcessor)
        class Wms (requestProcessor: OpenGisRequestProcessor, val layer: WmsLayer ) : Tile(requestProcessor)
    }

    class Feature(requestProcessor: OpenGisRequestProcessor, val featureType: FeatureType) : MapViewLayer(requestProcessor)
}