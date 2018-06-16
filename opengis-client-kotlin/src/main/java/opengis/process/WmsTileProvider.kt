package opengis.process

import geojson.BoundingBox
import opengis.model.app.CRS
import opengis.model.app.request.wms.Layer
import opengis.model.app.Style
import opengis.model.app.request.wms.GetMap
import opengis.process.projection.Projection
import opengis.process.projection.Tile

class WmsTileProvider(
        requestProcessor : OpenGisRequestProcessor,
        baseRequest      : GetMap<ByteArray>
): TileProvider<GetMap<ByteArray>>(requestProcessor, baseRequest ) {

    constructor(
            requestProcessor : OpenGisRequestProcessor,
            layerName        : String
    ) : this (
            requestProcessor = requestProcessor,
            styledLayers     = listOf(GetMap.StyledLayer(Layer(layerName), Style.default))
    )

    constructor(
            requestProcessor : OpenGisRequestProcessor,
            styledLayers     : List<GetMap.StyledLayer>
    ) : this(
            requestProcessor = requestProcessor,
            baseRequest = buildBaseTileRequest(styledLayers)
    )

    companion object {

        fun buildBaseTileRequest(
                styledLayers : List<GetMap.StyledLayer>
        ) : GetMap<ByteArray> = GetMap(
                styledLayers = styledLayers,
                reference    = CRS.Layer( nameSpace = CRS.Namespace.EPSG.name, name = "900913"), // "4326" //"3857"),
                transparent  = true,
                boundingBox  = BoundingBox.Global  // Will be replaced in [getTileRequest]
        )
    }

    override fun getTileRequest(baseRequest: GetMap<ByteArray>, tile: Tile.Google) : GetMap<ByteArray> = baseRequest.copy(
            boundingBox = tile
                    .toBounds( projection = Projection.GlobalMercator )
                    .toGeoJsonBoundingBox()
    )
}
