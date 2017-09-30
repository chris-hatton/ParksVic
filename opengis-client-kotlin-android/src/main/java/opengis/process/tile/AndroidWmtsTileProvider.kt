package opengis.process.tile

import opengis.model.Layer
import opengis.model.MimeType
import opengis.model.Style
import opengis.model.request.wmts.GetTile
import opengis.process.AndroidOpenGisClient

/**
 * Created by Chris on 24/09/2017.
 */
class AndroidWmtsTileProvider(
                    client           : AndroidOpenGisClient,
                    baseRequest      : GetTile<ByteArray>,
        private val tileMatrixMapper : (Int)->String = DefaultGoogleTileMatrixMapper
): OpenGisTileProvider<GetTile<ByteArray>>( client, baseRequest ) {

    constructor(
            client           : AndroidOpenGisClient,
            layerName        : String,
            tileMatrixSet    : String = DefaultGoogleMatrixSetName,
            style            : String = "",
            tileMatrixMapper : (Int)->String = DefaultGoogleTileMatrixMapper
    ) : this(
        client           = client,
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
            layer         = Layer( layerName ),
            style         = Style( styleName ),
            format        = MimeType.PNG.string,
            tileMatrixSet = tileMatrixSet,
            tileMatrix    = 0.toString(),
            tileRow       = 0,
            tileCol       = 0
        )
    }

    override fun getTileRequest(
        baseRequest : GetTile<ByteArray>,
        x           : Int,
        y           : Int,
        zoom        : Int
    ): GetTile<ByteArray> = baseRequest.copy(
        tileCol    = x,
        tileRow    = y,
        tileMatrix = tileMatrixMapper(zoom)
    )
}