package opengis.process.tile

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import opengis.model.app.request.OpenGisRequest
import opengis.process.Callback
import opengis.process.OpenGisRequestProcessor
import opengis.process.Outcome
import opengis.process.execute


abstract class OpenGisTileProvider<TileRequest : OpenGisRequest<ByteArray>> internal constructor(
        private val requestProcessor: OpenGisRequestProcessor,
        private val baseRequest : TileRequest
) : TileProvider {

    sealed class Exception : kotlin.Exception() {
        class BadTileData( private val data: ByteArray ) : Exception() {
            val dataAsString : String get() = String( bytes = data )
        }
    }

    abstract fun getTileRequest( baseRequest: TileRequest, x: Int, y: Int, zoom: Int ) : TileRequest

    final override fun getTile(x: Int, y: Int, zoom: Int): Tile {

        val monitor = Object()

        synchronized(monitor) {

            var isComplete = false
            var tile : Tile? = null

            val tileRequest : TileRequest = getTileRequest(baseRequest, x, y, zoom)

            val callback : Callback<ByteArray> = { outcome ->
                when(outcome) {
                    is Outcome.Success -> {
                        val result = outcome.result
                        synchronized(monitor) {

                            val decodeBoundsOptions = BitmapFactory.Options().apply {
                                inJustDecodeBounds = true
                            }

                            BitmapFactory.decodeByteArray(result,0,result.size,decodeBoundsOptions)

                            if( decodeBoundsOptions.outWidth == -1 || decodeBoundsOptions.outHeight == -1 ) {
                                throw Exception.BadTileData( data = result )
                            } else {
                                tile = Tile(decodeBoundsOptions.outWidth,decodeBoundsOptions.outHeight,result)
                            }

                            monitor.notify()
                            isComplete = true
                        }
                    }
                    is Outcome.Error -> {
                        synchronized(monitor) {
                            tile = null
                            monitor.notify()
                            isComplete = true
                        }
                    }
                }
            }

            requestProcessor.execute( tileRequest, callback )

            if(!isComplete) {
                monitor.wait()
            }

            return tile ?: TileProvider.NO_TILE
        }
    }
}