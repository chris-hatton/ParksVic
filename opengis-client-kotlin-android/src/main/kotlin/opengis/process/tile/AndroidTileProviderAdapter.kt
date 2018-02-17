package opengis.process.tile

import android.graphics.BitmapFactory
import opengis.model.app.request.OpenGisRequest
import opengis.process.Callback
import opengis.process.Outcome
import opengis.process.projection.Tile as OpenGisTile
import com.google.android.gms.maps.model.Tile as AndroidTile
import com.google.android.gms.maps.model.TileProvider as AndroidTileProvider
import opengis.ui.TileProvider as OpenGisTileProvider

/**
 * Abstraction of a TileProvider for an Android MapView,
 * one which sources image data from an OpenGIS service.
 *
 * Concrete implementations of this inc
 */
class AndroidTileProviderAdapter<TileRequest : OpenGisRequest<ByteArray>>(
        private val tileProvider : OpenGisTileProvider<TileRequest>
) : AndroidTileProvider {

    sealed class Exception : kotlin.Exception() {
        class BadTileData( private val data: ByteArray ) : Exception() {
            val dataAsString : String get() = String( bytes = data )
        }
    }

    override fun getTile(x: Int, y: Int, zoom: Int) : AndroidTile {

        val monitor = Object()

        synchronized(monitor) {

            var isComplete = false
            var outputTile : AndroidTile? = null

            val inputTile = OpenGisTile.Google(x,y,zoom)

            tileProvider.getTile( tile = inputTile ) { outcome ->
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
                                outputTile = AndroidTile(decodeBoundsOptions.outWidth,decodeBoundsOptions.outHeight,result)
                            }

                            monitor.notify()
                            isComplete = true
                        }
                    }
                    is Outcome.Error -> {
                        synchronized(monitor) {
                            outputTile = null
                            monitor.notify()
                            isComplete = true
                        }
                    }
                }
            }

            if(!isComplete) {
                monitor.wait()
            }

            return outputTile ?: AndroidTileProvider.NO_TILE
        }
    }
}