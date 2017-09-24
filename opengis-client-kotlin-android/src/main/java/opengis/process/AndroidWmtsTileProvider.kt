package opengis.process

import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import opengis.model.Layer
import opengis.model.Style
import opengis.model.request.wms.GetMap
import opengis.model.request.wmts.GetTile

/**
 * Created by Chris on 24/09/2017.
 */
class AndroidWmtsTileProvider(private val client: AndroidOpenGisClient, private val baseRequest: GetTile<Tile> ) : TileProvider {

    constructor(
        client    : AndroidOpenGisClient,
        layerName : String
    ) : this(
        client      = client,
        baseRequest = buildTileRequest(layerName)
    )

    companion object {
        fun buildTileRequest( layerName: String ) : GetTile<Tile> = GetTile(
            layer         = Layer( layerName ),
            style         = Style( "" ),
            format        = "",
            tileMatrixSet = "",
            tileMatrix    = 0.toString(),
            tileRow       = 0,
            tileCol       = 0
        )
    }

    sealed class Exception : kotlin.Exception() {
        object TileUnavailable : Exception()
    }

    private val monitor = Object()

    override fun getTile(x: Int, y: Int, zoom: Int): Tile {
        synchronized(monitor) {
            var tile : Tile? = null
            val tileRequest = baseRequest.copy(
                tileCol = x,
                tileRow = y,
                tileMatrix = zoom.toString()
            )

            val callback = object : OpenGisClient.Callback<Tile> {
                override fun success(result: Tile) {
                    synchronized(monitor) {
                        tile = result
                        monitor.notify()
                    }
                }

                override fun error(error: Throwable) {
                    synchronized(monitor) {
                        tile = null
                        monitor.notify()
                    }
                }
            }

            client.execute( tileRequest, callback )

            monitor.wait()
            return tile ?: throw Exception.TileUnavailable
        }
    }
}