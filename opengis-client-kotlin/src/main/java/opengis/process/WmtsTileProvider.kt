package opengis.process

import opengis.model.app.request.wmts.Layer
import opengis.model.app.MimeType
import opengis.model.app.Style
import opengis.model.app.request.wmts.GetTile
import opengis.process.projection.Tile

class WmtsTileProvider(
        requestProcessor : OpenGisRequestProcessor,
        baseRequest      : GetTile<ByteArray>,
        private val tileMatrixMapper : (Int)->String = DefaultGoogleTileMatrixMapper
): TileProvider<GetTile<ByteArray>>(requestProcessor, baseRequest ) {

    constructor(
            requestProcessor : OpenGisRequestProcessor,
            layerName        : String,
            tileMatrixSet    : String = DefaultGoogleMatrixSetName,
            style            : String = "",
            tileMatrixMapper : (Int)->String = DefaultGoogleTileMatrixMapper
    ) : this(
            requestProcessor = requestProcessor,
            baseRequest      = buildBaseTileRequest(layerName, tileMatrixSet, style),
            tileMatrixMapper = tileMatrixMapper
    )

    companion object {

        val DefaultGoogleTileMatrixMapper : (Int)->String = { "$DefaultGoogleMatrixSetName:$it" }
        val DefaultGoogleMatrixSetName = "EPSG:900913"

        fun buildBaseTileRequest(
                layerName     : String,
                tileMatrixSet : String,
                styleName     : String
        ) : GetTile<ByteArray> = GetTile(
                layer         = Layer(layerName),
                style         = Style(styleName),
                format        = MimeType.PNG.string,
                tileMatrixSet = tileMatrixSet,
                tileMatrix    = 0.toString(),
                tileRow       = 0,
                tileCol       = 0
        )
    }

    override fun getTileRequest(
            baseRequest : GetTile<ByteArray>,
            tile        : Tile.Google
    ): GetTile<ByteArray> = baseRequest.copy(
            tileCol    = tile.x,
            tileRow    = tile.y,
            tileMatrix = tileMatrixMapper(tile.zoom)
    )
}