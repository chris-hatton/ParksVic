package opengis.process

import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import opengis.model.request.wmts.GetTile

/**
 * Created by Chris on 24/09/2017.
 */
class AndroidTileProvider(private val client: AndroidOpenGisClient, private val baseRequest: GetTile<Tile> ) : TileProvider {

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
            client.execute(tileRequest) { tileIn:Tile ->
                synchronized(monitor) {
                    tile = tileIn
                    monitor.notify()
                }
            }
            monitor.wait()
            return tile ?: throw Exception.TileUnavailable
        }
    }
}