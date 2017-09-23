package kimage.process

import android.graphics.Bitmap
import kimage.model.Image
import kimage.model.pixel.RGB

class BitmapImageWrapper(val bitmap: Bitmap ) : Image<RGB> {

    override val width  : Int = bitmap.width
    override val height : Int = bitmap.height

    override fun get(x: Int, y: Int): RGB {
        val packedRGB = bitmap.getPixel(x,y)
        return colorToRGB(packedRGB)
    }
}