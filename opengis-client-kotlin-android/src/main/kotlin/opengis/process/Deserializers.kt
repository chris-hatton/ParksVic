package opengis.process

import android.content.Context
import android.graphics.BitmapFactory
import kimage.model.Image
import kimage.model.pixel.RGB
import kimage.process.BitmapImageWrapper

/**
 *
 */
fun createImageDeserializer( context: Context) : ResultDeserializer<Image<RGB>> = { bytes:ByteArray ->
    val bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.size )
    BitmapImageWrapper(bitmap)
}
