package opengis.process

import com.google.android.gms.maps.model.Tile
import opengis.model.request.OpenGisRequest
import opengis.model.request.wmts.GetTile
import kotlin.reflect.KClass

/**
 * Created by Chris on 24/09/2017.
 */
class AndroidMapTileDeserializer : OpenGisResponseDeserializer {
    override fun <Result : Any> deserializeResult(bytes: ByteArray, request: OpenGisRequest<Result>, resultClass: KClass<Result>): Result {

        val tileRequest : GetTile<Result> = request as? GetTile<Result> ?: throw OpenGisResponseDeserializer.Exception.UnhandledType

        if( resultClass != Tile::class ) throw OpenGisResponseDeserializer.Exception.UnhandledType

        val column = tileRequest.tileCol
        val row    = tileRequest.tileRow

        @Suppress("UNCHECKED_CAST")
        return Tile(column,row,bytes) as Result
    }
}