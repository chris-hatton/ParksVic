package opengis.process.deserialize.impl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import opengis.model.app.request.OpenGisRequest
import opengis.process.deserialize.OpenGisResponseDeserializer
import java.io.InputStream
import kotlin.reflect.KClass
import opengis.process.deserialize.OpenGisResponseDeserializer.Exception.*

/**
 * Created by Chris on 01/10/2017.
 */
class BitmapDeserializer : OpenGisResponseDeserializer {
    override fun <Result : Any> deserializeResult(
            bytes       : InputStream,
            request     : OpenGisRequest<Result>,
            resultClass : KClass<Result>
    ): Result = when(resultClass) {
        Bitmap::class -> {
            try {
                val byteArray : ByteArray = bytes.readBytes()
                BitmapFactory.decodeByteArray( byteArray, 0, byteArray.size ) as Result
            } catch(t: Throwable) {
                throw MalformedData
            }
        }
        else -> throw UnhandledType
    }
}
