package opengis.process.tile

import geojson.BoundingBox
import opengis.model.app.CRS
import opengis.model.app.Layer
import opengis.model.app.Style
import opengis.model.app.request.wms.GetMap
import opengis.process.AndroidOpenGisClient
import opengis.process.projection.Projection
import opengis.process.projection.Tile

// Construct with tile size in pixels, normally 256, see parent class.
class AndroidWmsTileProvider(
        client           : AndroidOpenGisClient,
        baseRequest      : GetMap<ByteArray>
): OpenGisTileProvider<GetMap<ByteArray>>( client, baseRequest ) {

    constructor(
        client    : AndroidOpenGisClient,
        layerName : String
    ) : this (
        client       = client,
        styledLayers = listOf(GetMap.StyledLayer(Layer(layerName), Style.default))
    )

    constructor(
        client       : AndroidOpenGisClient,
        styledLayers : List<GetMap.StyledLayer>
    ) : this(
        client      = client,
        baseRequest = AndroidWmsTileProvider.buildBaseTileRequest(styledLayers)
    )

    companion object {

        fun buildBaseTileRequest(
            styledLayers : List<GetMap.StyledLayer>
        ) : GetMap<ByteArray> = GetMap(
            styledLayers = styledLayers,
            reference    = CRS.Layer( nameSpace = CRS.Namespace.EPSG.name, name = "900913"), // "4326" //"3857"),
            transparent  = true,
            boundingBox  = BoundingBox.all  // Will be replaced in [getTileRequest]
        )
    }

    override fun getTileRequest(baseRequest: GetMap<ByteArray>, x: Int, y: Int, zoom: Int) : GetMap<ByteArray> = baseRequest.copy(
        boundingBox = Tile.Google(x,y,zoom)
                .toBounds( projection = Projection.GlobalMercator )
                .toGeoJsonBoundingBox()
    )
}
